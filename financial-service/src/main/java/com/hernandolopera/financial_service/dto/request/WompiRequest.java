package com.hernandolopera.financial_service.dto.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WompiRequest {

    private String name;

    private String description;

    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    private String currency;

    @JsonProperty("single_use")
    private Boolean singleUse;

    @JsonProperty("collect_shipping")
    private Boolean collectShipping;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    private String reference;

    // 🔥 METADATA PERSONALIZADA
    private Map<String, Object> metadata;
}