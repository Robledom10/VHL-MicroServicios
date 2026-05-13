package com.hernandolopera.auth_service.service.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.request.auth.RegisterRequest;
import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Servicio dedicado en exclusividad a manejar el registro de nuevos usuarios
 * validando datos y aplicando encriptación de seguridad en la contraseña.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza el flujo completo para persistir un nuevo cliente.
     * Verifica conflictos de email, encripta contraseñas y asigna el rol CLIENT por
     * defecto.
     *
     * @param request Datos de registro (modelo)
     * @return La entidad Usuario recién creada
     * @throws org.springframework.web.server.ResponseStatusException con código 409
     *                                                                si el email ya
     *                                                                existe
     */
    @Transactional
    public User registerUser(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo electrónico ya está registrado");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDocumentNumber(request.getDocumentNumber());
        user.setDocumentType(request.getDocumentType());
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());

        user.setProfileCompleted(false);

        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rol no encontrado"));

        user.setRole(role);

        return userRepository.save(user);
    }

    @Transactional
    public void completeProfile(String email, CompleteProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());

        // 🔥 ahora sí está completo
        user.setProfileCompleted(true);

        userRepository.save(user);
    }
}
