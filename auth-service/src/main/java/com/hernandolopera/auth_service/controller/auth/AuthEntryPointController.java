package com.hernandolopera.auth_service.controller.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.auth.ForgotPasswordRequest;
import com.hernandolopera.auth_service.dto.request.auth.GoogleLoginRequest;
import com.hernandolopera.auth_service.dto.request.auth.LoginRequest;
import com.hernandolopera.auth_service.dto.request.auth.RegisterRequest;
import com.hernandolopera.auth_service.dto.request.auth.ResetPasswordRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.service.auth.AuthService;
import com.hernandolopera.auth_service.service.auth.PasswordResetService;
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthEntryPointController {

    private final AuthService authService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Usuario registrado correctamente",
                "status", HttpStatus.CREATED.value()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse auth = authService.login(request);

        Cookie refreshCookie = new Cookie("refreshToken", auth.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // true en producción (HTTPS)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 días

        response.addCookie(refreshCookie);

        // ❗ NO enviar refresh token en body
        auth.setRefreshToken(null);

        return ResponseEntity.ok(auth);
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(
            @RequestBody GoogleLoginRequest request,
            HttpServletResponse response) {

        AuthResponse auth = authService.googleLogin(request);

        Cookie refreshCookie = new Cookie(
                "refreshToken",
                auth.getRefreshToken());

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // true en producción
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);

        auth.setRefreshToken(null);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(auth);
    }

    @GetMapping("/check-blacklist")
    public ResponseEntity<Boolean> checkBlacklist(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        String token = authHeader.substring(7);
        boolean isBlacklisted = blacklistedTokenService.isBlacklisted(token);
        return ResponseEntity.ok(isBlacklisted);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña",
                "status", HttpStatus.OK.value()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Contraseña actualizada correctamente",
                "status", HttpStatus.OK.value()));
    }
}
