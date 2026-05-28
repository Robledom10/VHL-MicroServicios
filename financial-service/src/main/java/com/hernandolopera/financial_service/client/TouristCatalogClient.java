package com.hernandolopera.financial_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.hernandolopera.financial_service.dto.response.TouristPackageResponse;

@Component
public class TouristCatalogClient {

    private final WebClient.Builder webClientBuilder;

    public TouristCatalogClient(
            WebClient.Builder webClientBuilder) {

        this.webClientBuilder = webClientBuilder;
    }

    public TouristPackageResponse getPackage(
            Integer packageId) {

        return webClientBuilder
                .build()
                .get()
                .uri(
                    "http://tourist-catalog-service:8082/api/packages/"
                    + packageId)
                .retrieve()
                .bodyToMono(TouristPackageResponse.class)
                .block();
    }
}