package com.hernandolopera.operation_servicio.transferencia;

import java.math.BigDecimal;

public record CatalogStatisticsResponse(

        long totalPackages,
        long activePackages,
        long inactivePackages,

        long totalProviders,
        long activeProviders,
        long inactiveProviders,

        BigDecimal averagePrice,
        BigDecimal maxPrice,
        BigDecimal minPrice

) {
}