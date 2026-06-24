package com.hernandolopera.auth_service.service.managment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hernandolopera.auth_service.dto.response.UserResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    /**
     * Obtener usuarios paginados
     */
    public Page<UserResponse> getAllUsers(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Obtener usuario por ID
     */
    public UserResponse getUserById(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToResponse(user);
    }

    /**
     * Deshabilitar usuario
     */
    public void disableUser(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(false);

        userRepository.save(user);
    }

    /**
     * Habilitar usuario
     */
    public void enableUser(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(true);

        userRepository.save(user);
    }

    /**
     * Convertir entidad User → UserResponse
     */
    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .documentType(user.getDocumentType() != null ? user.getDocumentType().name() : null)
                .documentNumber(user.getDocumentNumber())
                .role(user.getRole().getName())
                .active(user.getActive())
                .profileCompleted(user.getProfileCompleted())
                .build();
    }
}