package com.hernandolopera.auth_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.entity.RefreshToken;
import com.hernandolopera.auth_service.entity.User;
import com.hernandolopera.auth_service.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Crea y guarda un nuevo refresh token para el usuario
     * 
     * @param user
     * @return retorna el token refrescado
     */
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.findByUser(user)
                .ifPresent(existingToken -> refreshTokenRepository.delete(existingToken));

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Busca un refresh token por su valor String
     * 
     * @param token
     */
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token no valido"));
    }

    /**
     * Verifica si el token expiro
     * 
     * @param token
     */
    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado");
        }
    }

    /**
     * Elimina refresh token al cerrar sesión
     * 
     * @param user
     */
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}
