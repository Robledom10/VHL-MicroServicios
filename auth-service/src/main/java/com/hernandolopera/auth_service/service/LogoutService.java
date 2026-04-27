package com.hernandolopera.auth_service.service;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.LogoutRequest;
import com.hernandolopera.auth_service.entity.RefreshToken;
import com.hernandolopera.auth_service.entity.User;
import com.hernandolopera.auth_service.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    /**
     * Procesa el cierre de sesion del usuario
     * 
     * @param request contiene el refresh token enviado por el cliente
     */
    public void logout(LogoutRequest request) {

        /**
         * Buscar token existente
         */
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        /**
         * Validar expiracion
         */
        refreshTokenService.verifyExpiration(refreshToken);

        /**
         * Obtener usuario asociado
         */
        User user = refreshToken.getUser();

        /**
         * Eliminar refresh token
         */
        refreshTokenService.deleteByUser(user);
    }
}
