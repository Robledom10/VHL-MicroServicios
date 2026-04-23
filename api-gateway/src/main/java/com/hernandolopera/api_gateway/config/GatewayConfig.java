package com.hernandolopera.api_gateway.config;

import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

    // @Value("${microservices.auth-service-url}")
    // private String authServiceUrl;

    // @Bean
    // public RouteLocator authLocator(RouteLocatorBuilder builder) {
    // return builder.routes()
    // .route("auth-service", r -> r.path("/api/auth/**")
    // .uri(authServiceUrl))
    // .build();
    // }
}
