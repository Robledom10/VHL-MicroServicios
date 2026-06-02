package com.hernandolopera.auth_service.service.token;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.entity.token.RefreshToken;
import com.hernandolopera.auth_service.repository.token.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 🔹 Crear refresh token
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> {
                    RefreshToken newRefreshToken = new RefreshToken();
                    newRefreshToken.setUser(user);
                    return newRefreshToken;
                });
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    // 🔹 Validar refresh token
    public User validateRefreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Refresh token inválido"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expirado");
        }

        return token.getUser();
    }

    // 🔹 Eliminar refresh token
    public void deleteByToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Refresh token inválido"));
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expirado");
        }
    }
}
