package com.hernandolopera.api_gateway.security;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Filtro global de Gateway que intercepta todas las peticiones entrantes
 * para validar la existencia y validez de un token JWT en el encabezado de autorización.
 */
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Intercepta las solicitudes y verifica el token JWT.
     * Ignora las rutas públicas designadas. Si la solicitud no es pública y falta el token
     * o es inválido, rechaza la solicitud retornando un estado UNAUTHORIZED.
     *
     * @param exchange El entorno de la solicitud/respuesta web actual
     * @param chain    La cadena de filtros del gateway
     * @return Mono<Void> para indicar cuándo se completa el procesamiento de la solicitud
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        List<String> publicRoutes = List.of(
                "/api/auth/login",
                "/api/auth/register");

        boolean isPublic = path.equals("/api/auth/login") ||
                path.equals("/api/auth/register");

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            jwtTokenProvider.getEmailFromToken(token);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        System.out.println("GATEWAY PATH: [" + path + "]");

        return chain.filter(exchange);
    }

    /**
     * Configura el orden de ejecución de este filtro dentro de la cadena de filtros.
     * Un valor negativo asegura que se ejecute tempranamente.
     *
     * @return El orden del filtro (ej. -1)
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
