package com.hernandolopera.auth_service.controller;

import java.time.LocalDateTime;

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
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;
import com.hernandolopera.auth_service.service.token.RefreshAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/tokens")
@RequiredArgsConstructor
public class TokenManagementController {

    private final RefreshAuthService refreshAuthService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshAuthService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(token);
        blacklistedTokenService.blacklistToken(token, expirationDate);
        return ResponseEntity.ok("Logout exitoso");
    }

    @GetMapping("/check-blacklist")
    public ResponseEntity<Boolean> checkBlacklist(@RequestHeader("Authorization") String authHeader) {

        // 1. Validar que el encabezado no sea nulo y tenga el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(false);
        }

        // 2. EXTRAER EL TOKEN (Esto es lo que faltaba y causaba el error)
        String token = authHeader.substring(7);

        // 3. Consultar el servicio
        boolean isBlacklisted = blacklistedTokenService.isBlacklisted(token);

        return ResponseEntity.ok(isBlacklisted);
    }
}
