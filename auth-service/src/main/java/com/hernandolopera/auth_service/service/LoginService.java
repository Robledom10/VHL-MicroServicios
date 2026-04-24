package com.hernandolopera.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.LoginRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.User;
import com.hernandolopera.auth_service.repository.UserRepository;
import com.hernandolopera.auth_service.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado exclusivamente del inicio de sesión (Login) de los usuarios.
 */
@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Comprueba las credenciales del usuario y genera un token JWT si son correctas.
     *
     * @param request Datos de la petición conteniendo email y contraseña
     * @return Respuesta que contiene el token JWT generado
     * @throws RuntimeException si las credenciales son inválidas
     */
    public AuthResponse login(LoginRequest request) {

        String emailLimpio = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(emailLimpio)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        // System.out.println("TOKEN: " + token);

        return new AuthResponse(token);
    }
}
