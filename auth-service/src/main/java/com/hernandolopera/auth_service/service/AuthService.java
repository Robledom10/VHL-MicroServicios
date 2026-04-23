package com.hernandolopera.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.request.LoginRequest;
import com.hernandolopera.auth_service.dto.request.RegisterRequest;
import com.hernandolopera.auth_service.dto.response.AuthResponse;
import com.hernandolopera.auth_service.entity.Role;
import com.hernandolopera.auth_service.entity.User;
import com.hernandolopera.auth_service.repository.PermissionRepository;
import com.hernandolopera.auth_service.repository.RoleRepository;
import com.hernandolopera.auth_service.repository.UserRepository;
import com.hernandolopera.auth_service.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    // private final PermissionRepository permissionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public User registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya existe");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDocumentNumber(request.getDocumentNumber());

        user.setDocumentType(request.getDocumentType());

        Role role = roleRepository.findById(1).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.setRole(role);

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        String emailLimpio = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(emailLimpio)
                .orElseThrow(() -> new RuntimeException("Email invalido: [" + emailLimpio + "]"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        // System.out.println("TOKEN: " + token);

        return new AuthResponse(token);
    }
}
