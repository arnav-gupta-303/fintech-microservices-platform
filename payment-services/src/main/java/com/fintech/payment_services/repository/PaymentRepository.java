package com.fintech.payment_services.repository;

import com.fintech.payment_services.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(
            String senderEmail,
            String receiverEmail
    );
}