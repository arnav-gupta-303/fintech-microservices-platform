package com.fintech.notification_services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String senderEmail;

    private String receiverEmail;

    private BigDecimal amount;

    private String status;

    private String idempotencyKey;

    private String transactionType;

    private String description;

    private boolean refund;

    private String createdAt;
}
