package com.hernandolopera.auth_service.service.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.hernandolopera.auth_service.dto.request.auth.GoogleLoginRequest;
import com.hernandolopera.auth_service.dto.request.auth.LoginRequest;
import com.hernandolopera.auth_service.dto.request.auth.RegisterRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.token.RefreshToken;
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import com.hernandolopera.auth_service.repository.auth.UserRepository;
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

    // 🔥 NUEVOS
    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User registerUser(RegisterRequest request) {
        return registrationService.registerUser(request);
    }

    public AuthResponse login(LoginRequest request) {
        return loginService.login(request);
    }

    // 🔥 LOGIN GOOGLE
    public AuthResponse googleLogin(GoogleLoginRequest request) {

        try {

            Payload payload = googleAuthService.verifyToken(
                    request.getIdToken());

            if (payload == null) {
                throw new RuntimeException("Token Google inválido");
            }

            String email = payload.getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {

                        Role role = roleRepository.findByName("CLIENT")
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

                        User newUser = new User();

                        newUser.setEmail(email);

                        newUser.setFirstName(
                                (String) payload.get("given_name"));

                        newUser.setLastName(
                                (String) payload.get("family_name"));

                        newUser.setEmailVerified(true);

                        newUser.setProfileCompleted(false);

                        newUser.setActive(true);

                        newUser.setProvider("GOOGLE");

                        newUser.setAvatar(
                                (String) payload.get("picture"));

                        newUser.setRole(role);

                        return userRepository.save(newUser);
                    });

            String accessToken = jwtTokenProvider.generateToken(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error autenticando con Google");
        }
    }

    public void logout(String refreshToken, String accessToken) {

        if (refreshToken != null) {
            try {
                refreshTokenService.deleteByToken(refreshToken);
            } catch (Exception e) {
                System.out.println(
                        "Refresh token no encontrado o ya eliminado");
            }
        }

        LocalDateTime expirationDate = jwtTokenProvider.getExpirationDate(accessToken);

        blacklistedTokenService.blacklistToken(
                accessToken,
                expirationDate);
    }
}