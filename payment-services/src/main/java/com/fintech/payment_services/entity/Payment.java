package com.fintech.payment_services.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {

                @Index(
                        name = "idx_payment_sender",
                        columnList = "senderEmail"
                ),

                @Index(
                        name = "idx_payment_receiver",
                        columnList = "receiverEmail"
                ),

                @Index(
                        name = "idx_payment_created",
                        columnList = "createdAt"
                ),

                @Index(
                        name = "idx_payment_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_payment_idempotency",
                        columnList = "idempotencyKey",
                        unique = true
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderEmail;

    private String receiverEmail;

    private BigDecimal amount;

    private String status;

    private String failureReason;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }
    }
}
