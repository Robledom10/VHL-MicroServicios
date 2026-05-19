package com.hernandolopera.auth_service.service.managment;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.dto.response.ProfileResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse updateProfile(String email, CompleteProfileRequest request) {

        // 1. Buscar al usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Actualización Dinámica (Campo por Campo)
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getState() != null) {
            user.setState(request.getState());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getDocumentType() != null) {
            user.setDocumentType(request.getDocumentType());
        }
        if (request.getDocumentNumber() != null) {
            // Validar que el nuevo documento no pertenezca a otro usuario (UK)
            userRepository.findByDocumentNumber(request.getDocumentNumber())
                .ifPresent(u -> {
                    if (!u.getEmail().equals(email)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de documento ya está registrado");
                    }
                });
            user.setDocumentNumber(request.getDocumentNumber());
        }
        if (request.getEmail() != null && !request.getEmail().equals(email)) {
            // Validar que el nuevo email no esté duplicado si decide cambiarlo (UK)
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
            }
            user.setEmail(request.getEmail());
        }

        // Marcar el perfil como completado de manera persistente
        user.setProfileCompleted(true);

        // 3. Guardar cambios
        User updatedUser = userRepository.save(user);

        // 4. Construir la respuesta estructurada
        return ProfileResponse.builder()
                .id(updatedUser.getId())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .documentType(updatedUser.getDocumentType())
                .documentNumber(updatedUser.getDocumentNumber())
                .birthDate(updatedUser.getBirthDate())
                .state(updatedUser.getState())
                .city(updatedUser.getCity())
                .address(updatedUser.getAddress())
                .emailVerified(updatedUser.isEmailVerified())
                .phoneVerified(updatedUser.getPhoneVerified())
                .active(updatedUser.getActive())
                .role(updatedUser.getRole() != null ? updatedUser.getRole().getName() : "ROLE_CLIENT")
                .profileCompleted(updatedUser.getProfileCompleted())
                .build();
    }
}