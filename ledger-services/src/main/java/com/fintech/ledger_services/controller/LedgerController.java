package com.fintech.ledger_services.controller;

import com.fintech.ledger_services.dto.LedgerRequest;
import com.fintech.ledger_services.dto.LedgerResponse;
import com.fintech.ledger_services.entity.LedgerEntry;
import com.fintech.ledger_services.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/record")
    public ResponseEntity<LedgerResponse> record(
            @Valid @RequestBody LedgerRequest request
    ) {

        LedgerEntry entry = ledgerService.record(
                request.getEmail(),
                request.getType(),
                request.getAmount(),
                request.getReferenceId(),
                request.getDescription()
        );

        LedgerResponse response = LedgerResponse.builder()
                .id(entry.getId())
                .email(entry.getEmail())
                .type(entry.getType())
                .amount(entry.getAmount())
                .description(entry.getDescription())
                .referenceId(entry.getReferenceId())
                .createdAt(entry.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<LedgerResponse>> getHistory(
            @PathVariable String email
    ) {

        List<LedgerResponse> responses =
                ledgerService.getHistory(email)
                        .stream()
                        .map(entry -> LedgerResponse.builder()
                                .id(entry.getId())
                                .email(entry.getEmail())
                                .type(entry.getType())
                                .amount(entry.getAmount())
                                .description(entry.getDescription())
                                .referenceId(entry.getReferenceId())
                                .createdAt(entry.getCreatedAt())
                                .build())
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
