package com.hernandolopera.financial_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hernandolopera.financial_service.client.TouristCatalogClient;
import com.hernandolopera.financial_service.client.WompiClient;
import com.hernandolopera.financial_service.config.WompiProperties;
import com.hernandolopera.financial_service.domain.entity.Payment;
import com.hernandolopera.financial_service.domain.enums.PaymentStatus;
import com.hernandolopera.financial_service.dto.request.CreatePaymentRequest;
import com.hernandolopera.financial_service.dto.request.WompiRequest;
import com.hernandolopera.financial_service.dto.response.TouristPackageResponse;
import com.hernandolopera.financial_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final WompiClient wompiClient;

    private final WompiProperties properties;

    private final TouristCatalogClient touristCatalogClient;

    public Mono<String> createPaymentLink(
            CreatePaymentRequest request,
            String idempotencyKey) {

        // =========================
        // CONSULTAR PAQUETE TURÍSTICO
        // =========================

        TouristPackageResponse touristPackage =
                touristCatalogClient.getPackage(
                        request.getPackageId());

        if (touristPackage == null) {

            throw new IllegalStateException(
                    "Paquete turístico no encontrado");
        }

        // =========================
        // VALIDAR PRECIO
        // =========================

        if (touristPackage.getPrice() == null
                || touristPackage.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El paquete no tiene precio válido");
        }

        // =========================
        // GENERAR REFERENCIA
        // =========================

        String reference = generateReference();

        // =========================
        // CREAR PAYMENT LOCAL
        // =========================

        Payment payment = new Payment();

        payment.setFkIdAccount(
                request.getAccountId());

        // 🔥 PRECIO REAL DEL PAQUETE
        payment.setAmount(
                touristPackage.getPrice());

        payment.setPaymentMethod(
                request.getPaymentMethod());

        payment.setInstallmentNumber(
                request.getInstallments());

        payment.setReferenceNumber(
                reference);

        payment.setStatus(
                PaymentStatus.PENDIENTE);

        payment.setPaymentDate(
                LocalDateTime.now());

        payment.setCreatedAt(
                LocalDateTime.now());

        payment = paymentRepository.save(payment);

        System.out.println("REFERENCIA DB: " + reference);

        // =========================
        // WOMPI USA CENTAVOS
        // =========================

        Long amountInCents = payment.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // =========================
        // REQUEST WOMPI
        // =========================

        WompiRequest wompiRequest = new WompiRequest();

        wompiRequest.setName(
                touristPackage.getTitle());

        wompiRequest.setDescription(
                "Pago paquete turístico: "
                        + touristPackage.getTitle());

        wompiRequest.setAmountInCents(
                amountInCents);

        wompiRequest.setCurrency(
                "COP");

        wompiRequest.setSingleUse(
                true);

        wompiRequest.setCollectShipping(
                false);

        wompiRequest.setRedirectUrl(
                properties.getRedirectUrl());

        // =========================
        // METADATA
        // =========================

        Map<String, Object> metadata = new HashMap<>();

        metadata.put(
                "reference",
                payment.getReferenceNumber());

        metadata.put(
                "accountId",
                payment.getFkIdAccount());

        metadata.put(
                "installments",
                payment.getInstallmentNumber());

        metadata.put(
                "packageId",
                request.getPackageId());

        metadata.put(
                "packageTitle",
                touristPackage.getTitle());

        metadata.put(
                "amount",
                payment.getAmount());

        wompiRequest.setMetadata(metadata);

        // =========================
        // CREAR LINK WOMPI
        // =========================

        return wompiClient.createTransaction(wompiRequest)
                .map(response -> {

                    if (response == null
                            || !response.containsKey("data")) {

                        throw new IllegalStateException(
                                "Respuesta inválida de Wompi");
                    }

                    Map<String, Object> data =
                            (Map<String, Object>) response.get("data");

                    String linkId =
                            (String) data.get("id");

                    if (linkId == null) {

                        throw new IllegalStateException(
                                "Wompi no devolvió ID");
                    }

                    String paymentLink =
                            "https://checkout.wompi.co/l/" + linkId;

                    System.out.println("LINK WOMPI:");
                    System.out.println(paymentLink);

                    return paymentLink;
                });
    }

    private String generateReference() {

        return "PAY-"
                + System.currentTimeMillis()
                + "-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);
    }
}