package com.hernandolopera.auth_service.service.token;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.auth.RefreshTokenRequest;
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

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService
                .findByToken(request.getRefreshToken());

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().getName());

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken());
    }
}
