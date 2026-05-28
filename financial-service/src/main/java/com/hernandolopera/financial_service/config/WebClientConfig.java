package com.hernandolopera.financial_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();
    }

    @Bean
    public WebClient wompiWebClient(
            WompiProperties properties) {

        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}