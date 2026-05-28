package com.hernandolopera.financial_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hernandolopera.financial_service.dto.request.CreatePaymentRequest;
import com.hernandolopera.financial_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-link")
    public Mono<ResponseEntity<String>> create(
            @RequestBody CreatePaymentRequest request,
            @RequestHeader("idempotency-Key") String key) {
        return paymentService.createPaymentLink(request, key)
                .map(ResponseEntity::ok);
    }
}