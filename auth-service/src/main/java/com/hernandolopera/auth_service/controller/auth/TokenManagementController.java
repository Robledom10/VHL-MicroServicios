package com.hernandolopera.auth_service.controller.auth;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.auth.RefreshTokenRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.security.jwt.JwtTokenProvider;
import com.hernandolopera.auth_service.service.auth.AuthService;
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;
import com.hernandolopera.auth_service.service.token.RefreshAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/tokens")
@RequiredArgsConstructor
public class TokenManagementController {

    private final RefreshAuthService refreshAuthService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody(required = false) RefreshTokenRequest refreshBody,
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        // 1. Buscar en Cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // 2. Buscar en Body JSON (Soporte para Postman)
        if (refreshToken == null && refreshBody != null) {
            refreshToken = refreshBody.getRefreshToken();
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Ejecución de rotación en BD (SELECT -> DELETE -> INSERT)
        AuthResponse auth = refreshAuthService.refreshToken(refreshToken);

        // 🔥 Rotar Cookie con Path Unificado
        Cookie newCookie = new Cookie("refreshToken", auth.getRefreshToken());
        newCookie.setHttpOnly(true);
        newCookie.setSecure(false); // true en producción con HTTPS
        newCookie.setPath("/");    // 👈 Crucial para consistencia
        newCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(newCookie);
        auth.setRefreshToken(null); // No exponer en el JSON

        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response) {

        String accessToken = authHeader.substring(7);
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken != null) {
            authService.logout(refreshToken, accessToken);
        } else {
            LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(accessToken);
            blacklistedTokenService.blacklistToken(accessToken, expirationDate);
        }

        // 🔥 Borrar Cookie usando el mismo Path "/"
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout exitoso");
    }
}