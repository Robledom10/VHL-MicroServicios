package com.hernandolopera.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.LogoutRequest;
import com.hernandolopera.auth_service.entity.RefreshToken;
import com.hernandolopera.auth_service.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);

    private final RefreshTokenService refreshTokenService;

    /**
     * Procesa el cierre de sesión del usuario
     *
     * @param request contiene el refresh token enviado por el cliente
     */
    public void logout(LogoutRequest request) {

        /**
         * Buscar token existente
         */
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        /**
         * Validar expiración
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

        /**
         * Registrar evento de logout
         */
        logger.info(
                "Usuario {} cerró sesión correctamente",
                user.getEmail());
    }
}