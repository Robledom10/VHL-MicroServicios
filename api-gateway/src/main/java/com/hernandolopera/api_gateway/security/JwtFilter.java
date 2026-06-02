package com.hernandolopera.api_gateway.security;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Filtro global de Gateway que intercepta todas las peticiones entrantes
 * para validar la existencia y validez de un token JWT en el encabezado
 * Authorization.
 */
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient.Builder webClientBuilder;

    public JwtFilter(
            JwtTokenProvider jwtTokenProvider,
            WebClient.Builder webClientBuilder) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Intercepta las solicitudes y verifica el token JWT.
     * Ignora rutas públicas y valida blacklist + JWT para rutas protegidas.
     *
     * @param exchange entorno actual de request/response
     * @param chain    cadena de filtros del gateway
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        List<String> publicRoutes = List.of(
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/google-login",
                "/api/auth/tokens/refresh",
                "/api/auth/tokens/logout",
                "/api/auth/check-blacklist",
                "/api/webhook",
                "/api/media");

        boolean isPublic = publicRoutes.stream()
                .anyMatch(path::startsWith);
        boolean isPreflight = HttpMethod.OPTIONS.equals(method);
        boolean isPublicCatalogRead = HttpMethod.GET.equals(method)
                && (path.equals("/api/paquetes") || path.startsWith("/api/paquetes/"));

        if (isPublic || isPreflight || isPublicCatalogRead) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return webClientBuilder.build()
                .get()
                .uri("http://auth-service:8081/api/auth/check-blacklist")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    System.err.println("Error calling auth-service check-blacklist: " + e.getMessage());
                    return Mono.just(true); // Si falla, asumimos que está en blacklist por seguridad
                })
                .flatMap(isBlacklisted -> {

                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    System.out.println("GATEWAY PATH: [" + path + "]");
                    ServerWebExchange exchangeConUsuario = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Email", jwtTokenProvider.getEmailFromToken(token))
                                    .build())
                            .build();
                    return chain.filter(exchangeConUsuario);
                });
    }

    /**
     * Define el orden de ejecución del filtro.
     * Un valor negativo asegura ejecución temprana.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
