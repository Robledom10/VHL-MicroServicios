package com.hernandolopera.api_gateway.security;

import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient.Builder webClientBuilder;

    public JwtFilter(JwtTokenProvider jwtTokenProvider, WebClient.Builder webClientBuilder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        List<String> publicRoutes = List.of(
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/tokens/refresh",
                "/api/auth/logout",
                "/api/auth/check-blacklist");

        boolean isPublic = publicRoutes.stream().anyMatch(path::startsWith);

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "TOKEN_MISSING");
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            return unauthorized(exchange, "TOKEN_INVALID");
        }

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        // 🛡️ VALIDACIÓN BLACKLIST (Optimista)
        return webClientBuilder.build()
                .get()
                .uri("http://auth-service:8081/api/auth/check-blacklist")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(Boolean.class)
                // 🔥 CORRECCIÓN 1: Si falla la conexión, NO bloqueamos (false)
                .onErrorResume(e -> {
                    System.err.println("⚠️ Error conectando con Blacklist: " + e.getMessage());
                    return Mono.just(false); 
                })
                .flatMap(isBlacklisted -> {

                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        return unauthorized(exchange, "TOKEN_BLACKLISTED");
                    }

                    // 🛡️ AUTORIZACIÓN POR RUTA
                    if (path.startsWith("/api/auth/admin") && !roles.contains("ADMIN")) {
                        return forbidden(exchange);
                    }

                    if (path.startsWith("/api/guide") &&
                            !(roles.contains("ROLE_GUIDE") || roles.contains("ROLE_ADMIN"))) {
                        return forbidden(exchange);
                    }

                    // 🛡️ CORRECCIÓN 2: MUTATE con Header Relay
                    // Pasamos la info extra pero mantenemos el Authorization original
                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r.headers(headers -> {
                                headers.set("Authorization", authHeader); // Asegura el token para el microservicio
                                headers.add("X-User-Email", jwtTokenProvider.getEmailFromToken(token));
                                headers.add("X-User-Id", String.valueOf(jwtTokenProvider.getUserIdFromToken(token)));
                                headers.add("X-User-Roles", String.join(",", roles));
                            }))
                            .build();

                    System.out.println("✅ ACCESO PERMITIDO → " + path + " | Usuario: " + jwtTokenProvider.getEmailFromToken(token));

                    return chain.filter(mutated);
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String error) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // Opcional: podrías agregar el mensaje de error al header
        exchange.getResponse().getHeaders().add("X-Auth-Error", error);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}