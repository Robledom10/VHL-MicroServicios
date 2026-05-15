package com.hernandolopera.auth_service.service.managment;

import org.springframework.stereotype.Service;

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
    public ProfileResponse updateProfile(
            String email,
            CompleteProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());

        user.setProfileCompleted(true);

        User updatedUser = userRepository.save(user);

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
                .role(
                        updatedUser.getRole() != null
                                ? updatedUser.getRole().getName()
                                : "ROLE_CLIENT")
                .profileCompleted(updatedUser.getProfileCompleted())
                .build();
    }
}
