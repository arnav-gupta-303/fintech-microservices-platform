package com.fintech.ledger_services.repository;

import com.fintech.ledger_services.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerRepository
        extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry>
    findByEmailOrderByCreatedAtDesc(String email);

    boolean existsByReferenceId(String referenceId);
}
