package com.hernandolopera.financial_service.dto.request;

import com.hernandolopera.financial_service.domain.enums.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    private Integer accountId;

    private Integer packageId;

    private PaymentMethod paymentMethod;

    private Integer installments;
}