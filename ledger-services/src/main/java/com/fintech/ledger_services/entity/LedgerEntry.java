package com.fintech.ledger_services.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger_entries",
        indexes = {

                @Index(
                        name = "idx_ledger_email",
                        columnList = "email"
                ),

                @Index(
                        name = "idx_ledger_reference",
                        columnList = "referenceId"
                ),

                @Index(
                        name = "idx_ledger_created",
                        columnList = "createdAt"
                ),

                @Index(
                        name = "idx_ledger_type",
                        columnList = "type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String type;

    private BigDecimal amount;

    private String referenceId;

    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();
    }
}
