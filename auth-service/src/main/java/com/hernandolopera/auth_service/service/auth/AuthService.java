package com.hernandolopera.auth_service.service.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.auth.LoginRequest;
import com.hernandolopera.auth_service.dto.request.auth.RegisterRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.security.jwt.JwtTokenProvider;
import com.hernandolopera.auth_service.service.token.BlacklistedTokenService;
import com.hernandolopera.auth_service.service.token.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public User registerUser(RegisterRequest request) {
        return registrationService.registerUser(request);
    }

    public AuthResponse login(LoginRequest request) {
        return loginService.login(request);
    }

    public void logout(String refreshToken, String accessToken) {

        // 🔹 1. Eliminar refresh SOLO si existe
        if (refreshToken != null) {
            try {
                refreshTokenService.deleteByToken(refreshToken);
            } catch (Exception e) {
                // opcional: loggear, pero NO romper logout
                System.out.println("Refresh token no encontrado o ya eliminado");
            }
        }

        // 🔹 2. Siempre invalidar access token
        LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(accessToken);

        blacklistedTokenService.blacklistToken(accessToken, expirationDate);
    }
}