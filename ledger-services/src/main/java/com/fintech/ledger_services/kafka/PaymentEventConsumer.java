package com.fintech.ledger_services.kafka;

import com.fintech.ledger_services.dto.PaymentEvent;
import com.fintech.ledger_services.repository.LedgerRepository;
import com.fintech.ledger_services.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final LedgerService ledgerService;

    private final LedgerRepository ledgerRepository;

    @KafkaListener(
            topics = "payment-events",
            groupId = "ledger-group"
    )
    public void consume(PaymentEvent event) {

        log.info(
                "Received payment event: {}",
                event.getIdempotencyKey()
        );

        try {

            if (ledgerRepository.existsByReferenceId(
                    event.getIdempotencyKey()
            )) {

                log.warn(
                        "Duplicate event skipped: {}",
                        event.getIdempotencyKey()
                );

                return;
            }

            switch (event.getTransactionType()) {

                case "PAYMENT":

                    ledgerService.recordInternal(
                            event.getSenderEmail(),
                            "DEBIT",
                            event.getAmount(),
                            event.getIdempotencyKey(),
                            "Payment to "
                                    + event.getReceiverEmail()
                    );

                    ledgerService.recordInternal(
                            event.getReceiverEmail(),
                            "CREDIT",
                            event.getAmount(),
                            event.getIdempotencyKey() + "-credit",
                            "Payment from "
                                    + event.getSenderEmail()
                    );

                    break;

                case "REFUND":

                    ledgerService.recordInternal(
                            event.getSenderEmail(),
                            "CREDIT",
                            event.getAmount(),
                            event.getIdempotencyKey(),
                            "Refund issued"
                    );

                    break;

                case "FAILED_PAYMENT":

                    log.warn(
                            "Failed payment event: {}",
                            event.getIdempotencyKey()
                    );

                    break;

                default:

                    log.warn(
                            "Unknown transaction type: {}",
                            event.getTransactionType()
                    );
            }

        } catch (Exception e) {

            log.error(
                    "Error processing payment event: {}",
                    e.getMessage()
            );
        }
    }
}
