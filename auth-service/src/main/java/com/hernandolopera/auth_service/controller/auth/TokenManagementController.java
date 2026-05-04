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
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.security.jwt.JwtTokenProvider;
import com.hernandolopera.auth_service.service.auth.AuthService;
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;
import com.hernandolopera.auth_service.service.token.RefreshAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthResponse auth = refreshAuthService.refreshToken(refreshToken);

        // 🔥 ROTAR COOKIE
        Cookie newCookie = new Cookie("refreshToken", auth.getRefreshToken());
        newCookie.setHttpOnly(true);
        newCookie.setSecure(false);
        newCookie.setPath("/api/auth/tokens");
        newCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(newCookie);

        // ❗ ocultar refresh
        auth.setRefreshToken(null);

        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }

        String accessToken = authHeader.substring(7);

        // 🔹 obtener refresh cookie
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // 🔥 llamar service (eliminas refresh + blacklist)
        if (refreshToken != null) {
            authService.logout(refreshToken, accessToken);
        } else {
            LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(accessToken);
            blacklistedTokenService.blacklistToken(accessToken, expirationDate);
        }

        // 🔥 borrar cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth/tokens");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

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
