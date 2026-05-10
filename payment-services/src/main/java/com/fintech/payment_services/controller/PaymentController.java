package com.fintech.payment_services.controller;

import com.fintech.payment_services.dto.PaymentRequest;
import com.fintech.payment_services.dto.PaymentResponse;
import com.fintech.payment_services.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/send")
    public ResponseEntity<PaymentResponse> send(

            @RequestHeader("X-Authenticated-User")
            String email,

            @RequestBody PaymentRequest request,

            @RequestHeader("Authorization")
            String token
    ) {

        return ResponseEntity.ok(
                paymentService.processPayment(
                        email,
                        request,
                        token
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponse>> history(

            @RequestHeader("X-Authenticated-User")
            String email
    ) {

        return ResponseEntity.ok(
                paymentService.getHistory(email)
        );
    }
}
