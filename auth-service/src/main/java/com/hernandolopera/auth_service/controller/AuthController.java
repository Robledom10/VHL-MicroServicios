package com.hernandolopera.auth_service.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.LoginRequest;
import com.hernandolopera.auth_service.dto.request.RefreshTokenRequest;
import com.hernandolopera.auth_service.dto.request.RegisterRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.security.JwtTokenProvider;
import com.hernandolopera.auth_service.service.AuthService;
import com.hernandolopera.auth_service.service.BlacklistedTokenService;
import com.hernandolopera.auth_service.service.RefreshAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador de entrada para las operaciones REST relacionadas con la
 * seguridad del usuario.
 * Proporciona endpoints para registro, inicio de sesión y obtención de roles.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshAuthService refreshAuthService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistedTokenService blacklistedTokenService;

    /**
     * Endpoint responsable de registrar o introducir un nuevo usuario en la base de
     * datos.
     *
     * @param request Contiene todos los campos necesarios listos en JSON
     * @return Mapa de datos con estado 201 indicando un registro exitoso
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Usuario registrado correctamente",
                "status", HttpStatus.CREATED.value()));
    }

    /**
     * Endpoint para iniciar sesión y emitir un token JWT válido.
     *
     * @param request Credenciales del usuario preexistente
     * @return Entidad JSON con el token codificado
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint para consultar los detalles del usuario actualmente autenticado (mi
     * perfil).
     *
     * @param authentication El contexto de seguridad inyectado por spring
     * @return Un JSON resumiendo el email de usuario y roles
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario no autenticado"));
        }

        String email = authentication.getName();

        return ResponseEntity.ok(
                Map.of(
                        "email", email,
                        "message", "Usuario autenticado correctamente"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> admin() {
        return ResponseEntity.ok(
                Map.of(
                        "message", "Acceso autorizado para administrador"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @GetMapping("/user")
    public ResponseEntity<Map<String, String>> user() {
        return ResponseEntity.ok(
                Map.of(
                        "message", "Acceso autorizado para usuario autenticado"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshAuthService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }

        String token = authHeader.substring(7);

        LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(token);

        blacklistedTokenService.blacklistToken(token, expirationDate);

        return ResponseEntity.ok("Logout exitoso");
    }

    @GetMapping("/check-blacklist")
    public ResponseEntity<Boolean> checkBlacklist(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(false);
        }

        String token = authHeader.substring(7);

        boolean isBlacklisted = blacklistedTokenService.isBlacklisted(token);

        return ResponseEntity.ok(isBlacklisted);
    }

}
