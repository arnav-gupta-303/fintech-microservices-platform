package com.fintech.wallet_services.service;

import com.fintech.wallet_services.dto.*;
import com.fintech.wallet_services.entity.Wallet;
import com.fintech.wallet_services.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletResponse createWallet(String email) {

        if (walletRepository.findByEmail(email).isPresent()) {

            throw new RuntimeException("Wallet already exists");
        }

        Wallet wallet = Wallet.builder()
                .email(email)
                .build();

        walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    public WalletResponse getWallet(String email) {

        Wallet wallet = walletRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found")
                );

        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse topUp(
            String email,
            TopUpRequest request
    ) {

        Wallet wallet = walletRepository.findByEmailWithLock(email)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found")
                );

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse debit(
            String email,
            DebitRequest request
    ) {

        Wallet wallet = walletRepository.findByEmailWithLock(email)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found")
                );

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {

            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(request.getAmount())
        );

        walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse credit(
            String email,
            BigDecimal amount
    ) {

        Wallet wallet = walletRepository.findByEmailWithLock(email)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found")
                );

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    private WalletResponse mapToResponse(Wallet w) {

        return WalletResponse.builder()
                .id(w.getId())
                .email(w.getEmail())
                .balance(w.getBalance())
                .currency("USD")
                .createdAt(w.getCreatedAt())
                .build();
    }
}
