package com.fintech.wallet_services.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallets",
        indexes = {

                @Index(
                        name = "idx_wallet_email",
                        columnList = "email",
                        unique = true
                ),

                @Index(
                        name = "idx_wallet_created",
                        columnList = "createdAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private BigDecimal balance;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (balance == null) {

            balance = BigDecimal.ZERO;
        }
    }
}
