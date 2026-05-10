package com.fintech.payment_services.service;

import com.fintech.payment_services.dto.*;
import com.fintech.payment_services.entity.Payment;
import com.fintech.payment_services.feign.FraudClient;
import com.fintech.payment_services.feign.WalletClient;
import com.fintech.payment_services.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final WalletClient walletClient;

    private final FraudClient fraudClient;

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Transactional
    public PaymentResponse processPayment(
            String senderEmail,
            PaymentRequest request,
            String token
    ) {

        var existing =
                paymentRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        Payment payment = Payment.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .senderEmail(senderEmail)
                .receiverEmail(request.getReceiverEmail())
                .amount(request.getAmount())
                .build();

        paymentRepository.save(payment);

        try {

            Map<String, Object> fraudReq = Map.of(
                    "email", senderEmail,
                    "amount", request.getAmount(),
                    "referenceId", request.getIdempotencyKey()
            );

            Map<String, String> fraudResult =
                    fraudClient.check(token, fraudReq);

            if ("REJECTED".equals(fraudResult.get("result"))) {

                payment.setStatus("FAILED");

                payment.setFailureReason(
                        fraudResult.get("reason")
                );

                paymentRepository.save(payment);

                publishFailureEvent(payment);

                return mapToResponse(payment);
            }

            walletClient.debit(
                    token,
                    senderEmail,
                    request.getAmount(),
                    request.getIdempotencyKey()
            );

            walletClient.credit(
                    token,
                    request.getReceiverEmail(),
                    request.getAmount()
            );

            payment.setStatus("SUCCESS");

            paymentRepository.save(payment);

            publishSuccessEvents(payment);

            return mapToResponse(payment);

        } catch (Exception e) {

            log.error("Payment failed: {}", e.getMessage());

            try {

                walletClient.credit(
                        token,
                        senderEmail,
                        request.getAmount()
                );

                publishRefundEvent(payment);

                log.info(
                        "Compensating credit issued to {}",
                        senderEmail
                );

            } catch (Exception ex) {

                log.error(
                        "Compensation failed: {}",
                        ex.getMessage()
                );
            }

            payment.setStatus("FAILED");

            payment.setFailureReason(e.getMessage());

            paymentRepository.save(payment);

            publishFailureEvent(payment);

            return mapToResponse(payment);
        }
    }

    public List<PaymentResponse> getHistory(String email) {

        return paymentRepository
                .findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(
                        email,
                        email
                )
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void publishSuccessEvents(Payment p) {

        kafkaTemplate.send(
                "payment-events",
                PaymentEvent.builder()
                        .senderEmail(p.getSenderEmail())
                        .receiverEmail(p.getReceiverEmail())
                        .amount(p.getAmount())
                        .status("SUCCESS")
                        .transactionType("PAYMENT")
                        .description(
                                "Payment from "
                                        + p.getSenderEmail()
                                        + " to "
                                        + p.getReceiverEmail()
                        )
                        .refund(false)
                        .idempotencyKey(p.getIdempotencyKey())
                        .createdAt(LocalDateTime.now().toString())
                        .build()
        );
    }

    private void publishFailureEvent(Payment p) {

        kafkaTemplate.send(
                "payment-events",
                PaymentEvent.builder()
                        .senderEmail(p.getSenderEmail())
                        .receiverEmail(p.getReceiverEmail())
                        .amount(p.getAmount())
                        .status("FAILED")
                        .transactionType("FAILED_PAYMENT")
                        .description(
                                "Failed payment from "
                                        + p.getSenderEmail()
                        )
                        .refund(false)
                        .idempotencyKey(p.getIdempotencyKey())
                        .createdAt(LocalDateTime.now().toString())
                        .build()
        );
    }

    private void publishRefundEvent(Payment p) {

        kafkaTemplate.send(
                "payment-events",
                PaymentEvent.builder()
                        .senderEmail(p.getSenderEmail())
                        .receiverEmail(p.getReceiverEmail())
                        .amount(p.getAmount())
                        .status("REFUND")
                        .transactionType("REFUND")
                        .description(
                                "Refund issued to "
                                        + p.getSenderEmail()
                        )
                        .refund(true)
                        .idempotencyKey(
                                p.getIdempotencyKey() + "-refund"
                        )
                        .createdAt(LocalDateTime.now().toString())
                        .build()
        );
    }

    private PaymentResponse mapToResponse(Payment p) {

        return PaymentResponse.builder()
                .id(p.getId())
                .idempotencyKey(p.getIdempotencyKey())
                .senderEmail(p.getSenderEmail())
                .receiverEmail(p.getReceiverEmail())
                .amount(p.getAmount())
                .status(p.getStatus())
                .failureReason(p.getFailureReason())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
