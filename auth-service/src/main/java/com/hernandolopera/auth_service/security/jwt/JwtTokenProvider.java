package com.hernandolopera.auth_service.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Proveedor utilitario para la generación y validación de tokens JWT en el
 * microservicio de autenticación.
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
     * Genera un nuevo token JWT utilizando el email del usuario como sujeto
     * (subject).
     *
     * @param email Email a incrustar en el token
     * @return El token en formato String
     */
    public String generateToken(Integer userId, String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", roles)
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

    /**
     * Verifica la validez de un token y obtiene el respectivo id
     * 
     * @param token
     * @return El id almacenado en el token
     * @throws io.jsonwebtoken.ClaimJwtException si el token esta manipulado o
     *                                           expirado
     */

    public Integer getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Integer.class);
    }

    /**
     * Verifica la validez de un token y obtiene el respectivo role
     * 
     * @param token
     * @return El role almacenado en el token
     * @throws io.jsonwebtoken.ClaimJwtException si el token esta manipulado o
     *                                           expirado
     */

    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    /**
     * Obtiene la fecha de expiración del JWT.
     * Esto se usa para guardar el token en blacklist
     * hasta que expire naturalmente.
     *
     * @param token JWT recibido
     * @return fecha de expiración del token
     */
    public LocalDateTime getExpirationDate(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}