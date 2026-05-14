package com.hernandolopera.auth_service.service.managment;

import java.util.List;

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
     * Obtener todos los usuarios
     */
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
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
                .role(user.getRole().getName())
                .active(user.getActive())
                .profileCompleted(user.getProfileCompleted())
                .build();
    }
}
