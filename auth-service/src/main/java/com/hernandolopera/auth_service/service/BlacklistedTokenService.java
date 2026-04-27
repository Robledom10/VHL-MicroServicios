package com.hernandolopera.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.entity.BlacklistedToken;
import com.hernandolopera.auth_service.repository.BlacklistedTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Guarda un token JWT en blacklist.
     *
     * @param token          JWT a invalidar
     * @param expirationDate fecha de expiración original del token
     */
    public void blacklistToken(String token, LocalDateTime expirationDate) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setLogoutAt(LocalDateTime.now());
        blacklistedToken.setExpiresAt(expirationDate);

        blacklistedTokenRepository.save(blacklistedToken);

    }

    /**
     * Verifica si un token ya fue invalidado.
     *
     * @param token JWT recibido
     * @return true si está en blacklist
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
