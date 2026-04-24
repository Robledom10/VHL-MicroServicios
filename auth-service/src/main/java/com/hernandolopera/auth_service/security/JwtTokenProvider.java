package com.hernandolopera.auth_service.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Proveedor utilitario para la generación y validación de tokens JWT en el microservicio de autenticación.
 */
@Component
public class JwtTokenProvider {

    private final String JWT_SECRET;
    private final long JWT_EXPIRATION;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpiration) {
        this.JWT_SECRET = jwtSecret;
        this.JWT_EXPIRATION = jwtExpiration;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    /**
     * Genera un nuevo token JWT utilizando el email del usuario como sujeto (subject).
     *
     * @param email Email a incrustar en el token
     * @return El token en formato String
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifica la validez de un token y obtiene el respectivo email (subject).
     *
     * @param token El token recibido
     * @return El correo electrónico almacenado en el token
     * @throws io.jsonwebtoken.JwtException si el token está manipulado o expirado
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Nuevo método de verificación
                .build()
                .parseSignedClaims(token) // Parseo moderno
                .getPayload() // Obtenemos el cuerpo (payload)
                .getSubject();
    }
}