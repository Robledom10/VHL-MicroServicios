package com.hernandolopera.financial_service.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TouristPackageResponse {

    @JsonProperty("id")
    private Integer idPackage;

    @JsonProperty("titulo")
    private String title;

    @JsonProperty("precio")
    private BigDecimal price;
}
