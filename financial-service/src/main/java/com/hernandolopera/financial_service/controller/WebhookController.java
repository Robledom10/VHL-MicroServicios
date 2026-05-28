package com.hernandolopera.financial_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hernandolopera.financial_service.domain.entity.Payment;
import com.hernandolopera.financial_service.domain.enums.PaymentStatus;
import com.hernandolopera.financial_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentRepository paymentRepository;

    @PostMapping("/wompi")
    public ResponseEntity<Void> receive(
            @RequestBody Map<String, Object> payload) {

        System.out.println("🔥 WEBHOOK WOMPI RECIBIDO");

        try {

            // =========================
            // EXTRAER DATA
            // =========================

            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            Map<String, Object> transaction = (Map<String, Object>) data.get("transaction");

            // =========================
            // OBTENER REFERENCIA
            // =========================

            String reference = null;

            // 1. Intentar desde metadata
            Map<String, Object> metadata = (Map<String, Object>) transaction.get("metadata");

            if (metadata != null) {

                reference = (String) metadata.get("reference");
            }

            // 2. Fallback al reference raíz
            if (reference == null) {

                reference = (String) transaction.get("reference");
            }

            // =========================
            // STATUS
            // =========================

            String status = (String) transaction.get("status");

            // =========================
            // CUOTAS
            // =========================

            Map<String, Object> paymentMethod = (Map<String, Object>) transaction.get("payment_method");

            Integer installments = 1;

            if (paymentMethod != null
                    && paymentMethod.get("installments") != null) {

                installments = (Integer) paymentMethod.get("installments");
            }

            System.out.println("📌 REF: " + reference);
            System.out.println("📌 STATUS: " + status);
            System.out.println("📌 CUOTAS: " + installments);

            // =========================
            // VALIDAR REFERENCIA
            // =========================

            if (reference == null) {

                System.out.println("❌ REFERENCIA NULL");

                return ResponseEntity.ok().build();
            }

            // =========================
            // BUSCAR PAYMENT
            // =========================

            Payment payment = paymentRepository
                    .findByReferenceNumber(reference)
                    .orElse(null);

            if (payment == null) {

                System.out.println("❌ PAGO NO ENCONTRADO");

                return ResponseEntity.ok().build();
            }

            // =========================
            // EVITAR DUPLICADOS
            // =========================

            if (payment.getStatus() == PaymentStatus.APROBADO) {

                System.out.println("⚠️ PAGO YA PROCESADO");

                return ResponseEntity.ok().build();
            }

            // =========================
            // ACTUALIZAR STATUS
            // =========================

            if (isApproved(status)) {

                payment.setStatus(PaymentStatus.APROBADO);

                System.out.println("PAGO APROBADO");

            } else {

                payment.setStatus(PaymentStatus.RECHAZADO);

                System.out.println("PAGO RECHAZADO");
            }

            // =========================
            // ACTUALIZAR CUOTAS
            // =========================

            payment.setInstallmentNumber(installments);

            // =========================
            // GUARDAR
            // =========================

            paymentRepository.save(payment);

        } catch (Exception e) {

            System.out.println("ERROR WEBHOOK");

            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    private boolean isApproved(String status) {

        return switch (status.toUpperCase()) {

            case "APPROVED",
                    "PAGADO",
                    "APROBADO" ->
                true;

            default -> false;
        };
    }
}