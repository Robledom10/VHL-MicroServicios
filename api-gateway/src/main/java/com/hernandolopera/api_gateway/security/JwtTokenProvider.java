package com.hernandolopera.api_gateway.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Proveedor utilitario para la generación y validación de tokens JWT en el API
 * Gateway.
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
     * Genera un nuevo token JWT para un usuario en específico.
     *
     * @param email El correo electrónico del usuario, que se utilizará como
     *              "subject" del token
     * @return El token JWT generado y firmado
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
     * Extrae el correo electrónico (subject) contenido dentro del token JWT.
     * También verifica que el token esté correctamente firmado y no haya expirado.
     *
     * @param token El token JWT codificado a evaluar
     * @return El correo electrónico extraído del token
     * @throws io.jsonwebtoken.JwtException si el token es inválido, está expirado o
     *                                      malformado
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Nuevo método de verificación
                .build()
                .parseSignedClaims(token) // Parseo moderno
                .getPayload() // Obtenemos el cuerpo (payload)
                .getSubject();
    }

    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String firstName = claims.get("firstName", String.class);
        String lastName = claims.get("lastName", String.class);
        String fullName = String.join(" ",
                firstName == null ? "" : firstName.trim(),
                lastName == null ? "" : lastName.trim()).trim();

        return fullName.isBlank() ? claims.getSubject() : fullName;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
