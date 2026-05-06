package com.hernandolopera.auth_service.service.token;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.entity.token.RefreshToken;
import com.hernandolopera.auth_service.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshAuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse refreshToken(String refreshTokenValue) {

        RefreshToken oldToken;

        try {
            oldToken = refreshTokenService.findByToken(refreshTokenValue);
        } catch (Exception e) {
            // 💥 TOKEN NO EXISTE → POSIBLE ROBO
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Posible robo de sesión. Vuelve a iniciar sesión.");
        }

        refreshTokenService.verifyExpiration(oldToken);

        User user = oldToken.getUser();

        // 🔥 ROTACIÓN
        refreshTokenService.deleteByToken(refreshTokenValue);
        RefreshToken newToken = refreshTokenService.createRefreshToken(user);

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .toList();

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                roles);

        return new AuthResponse(newAccessToken, newToken.getToken());
    }
}