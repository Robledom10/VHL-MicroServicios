package com.hernandolopera.financial_service.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TouristPackageResponse {
    private Integer idPackage;
    private String title;
    private BigDecimal price;
}
