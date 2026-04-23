package com.hernandolopera.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable()) // <--- AQUÍ DESACTIVAS EL CSRF EN EL GATEWAY
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll() // Permitimos todo el paso por el gateway
            );
        return http.build();
    }
}
