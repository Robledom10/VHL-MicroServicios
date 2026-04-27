package com.hernandolopera.auth_service.service;

import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.RefreshTokenRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.RefreshToken;
import com.hernandolopera.auth_service.entity.User;
import com.hernandolopera.auth_service.security.JwtTokenProvider;

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
