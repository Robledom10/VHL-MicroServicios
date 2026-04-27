package com.hernandolopera.auth_service.service;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.LoginRequest;
import com.hernandolopera.auth_service.dto.request.RegisterRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.User;

import lombok.RequiredArgsConstructor;

/**
 * Servicio facade que agrupa las operaciones principales de autenticación y
 * registro.
 * Delega responsabilidades a los respectivos servicios específicos
 * ({@link LoginService} y {@link RegistrationService}).
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro del usuario
     * @return El usuario guardado
     */
    public User registerUser(RegisterRequest request) {
        return registrationService.registerUser(request);
    }

    /**
     * Autentica a un usuario y le proporciona un token JWT.
     *
     * @param request Credenciales del usuario (email y contraseña)
     * @return Token de autenticación (AuthResponse)
     */
    public AuthResponse login(LoginRequest request) {
        return loginService.login(request);
    }

    public void logout(String refreshToken, String accessToken) {
        refreshTokenService.logout(refreshToken, accessToken);
    }
}