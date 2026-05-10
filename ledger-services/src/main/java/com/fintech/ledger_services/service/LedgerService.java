package com.fintech.ledger_services.service;

import com.fintech.ledger_services.entity.LedgerEntry;
import com.fintech.ledger_services.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    public LedgerEntry record(
            String email,
            String type,
            BigDecimal amount,
            String referenceId,
            String description
    ) {

        LedgerEntry entry = LedgerEntry.builder()
                .email(email)
                .type(type)
                .amount(amount)
                .referenceId(referenceId)
                .description(description)
                .build();

        return ledgerRepository.save(entry);
    }

    public void recordInternal(
            String email,
            String type,
            BigDecimal amount,
            String referenceId,
            String description
    ) {

        LedgerEntry entry = LedgerEntry.builder()
                .email(email)
                .type(type)
                .amount(amount)
                .referenceId(referenceId)
                .description(description)
                .build();

        ledgerRepository.save(entry);
    }

    public List<LedgerEntry> getHistory(String email) {

        return ledgerRepository
                .findByEmailOrderByCreatedAtDesc(email);
    }
}
