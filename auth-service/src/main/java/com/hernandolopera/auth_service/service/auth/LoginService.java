package com.hernandolopera.auth_service.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import com.hernandolopera.auth_service.dto.request.auth.LoginRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.entity.token.RefreshToken;
import com.hernandolopera.auth_service.repository.auth.UserRepository;
import com.hernandolopera.auth_service.security.jwt.JwtTokenProvider;
import com.hernandolopera.auth_service.service.security.LoginAttemptService;
import com.hernandolopera.auth_service.service.token.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final RefreshTokenService refreshTokenService;
        private final LoginAttemptService loginAttemptService;

        public AuthResponse login(LoginRequest request) {

                String emailLimpio = request.getEmail()
                                .trim()
                                .toLowerCase();

                User user = userRepository.findByEmail(emailLimpio)
                                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

                // 🔒 Validar estado de cuenta
                if (Boolean.FALSE.equals(user.getActive())) {
                        throw new RuntimeException("La cuenta se encuentra inactiva.");
                }

                // 🔒 Validar bloqueo
                if (Boolean.FALSE.equals(user.getAccountNonLocked())) {

                        boolean unlocked = loginAttemptService.unlockWhenTimeExpired(user);

                        if (!unlocked) {
                                throw new RuntimeException("Cuenta bloqueada temporalmente.");
                        }
                }

                // 🔑 Validar contraseña
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                        loginAttemptService.increaseFailedAttemps(user);

                        throw new RuntimeException("Credenciales inválidas");
                }

                // ✅ Login OK → reset intentos
                loginAttemptService.resetFailedAttemps(user);

                // 🔥 ROLES (clave para todo tu sistema)
                String roles = user.getRoles().getName();

                // 🔐 JWT
                String accessToken = jwtTokenProvider.generateToken(
                                user.getId(),
                                user.getEmail(),
                                roles);

                // 🔁 Refresh Token
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                return new AuthResponse(
                                accessToken,
                                refreshToken.getToken());
        }
}