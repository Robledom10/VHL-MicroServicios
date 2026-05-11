package com.hernandolopera.auth_service.service.auth;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.response.UserResponse;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        // .toList() es preferible en Java 16+ para inmutabilidad
        return userRepository.findAll().stream()
                .map(this::map)
                .toList();
    }

    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return map(user);
    }

    @Transactional
    public UserResponse disableUser(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        user.setActive(false);
        User updatedUser = userRepository.save(user);

        userRepository.save(user);

        return map(updatedUser);
    }

    @Transactional
    public UserResponse enableUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setActive(true);

        User updatedUser = userRepository.save(user);
        userRepository.save(user);

        return map(updatedUser);
    }

    private UserResponse map(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        // Ajustado a tus campos específicos
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        response.setDocumentType(user.getDocumentType());
        response.setDocumentNumber(user.getDocumentNumber());

        response.setBirthDate(user.getBirthDate());

        response.setState(user.getState());
        response.setCity(user.getCity());
        response.setAddress(user.getAddress());

        response.setActive(user.getActive());
        response.setProfileCompleted(user.getProfileCompleted());

        // 🔥 Como en el DTO usas Set<String> roles:
        if (user.getRoles() != null) {
            response.setRoles(Set.of(user.getRoles().getName()));
        }

        return response;
    }
}
