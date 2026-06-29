package com.hernandolopera.api_gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para el API Gateway utilizando Spring WebFlux.
 * Centraliza las políticas de seguridad a nivel de enrutamiento principal.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    /**
     * Define la cadena de filtros de seguridad para las solicitudes web.
     * Desactiva CSRF y permite inicialmente el paso de todas las solicitudes,
     * delegando la validación del token al filtro personalizado {@link JwtFilter}.
     *
     * @param http Configuración de seguridad HTTP del servidor
     * @return instacia configurada de SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // CorsWebFilter handles CORS before Spring Security
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll()
            );
        return http.build();
    }
}
