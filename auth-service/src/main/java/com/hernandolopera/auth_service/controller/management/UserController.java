package com.hernandolopera.auth_service.controller.management;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.security.details.CustomUserDetails;
import com.hernandolopera.auth_service.service.managment.ProfileService;
import com.hernandolopera.auth_service.service.auth.RegistrationService;
import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.dto.response.ProfileResponse;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/profile")
@RequiredArgsConstructor
public class UserController {

    private final RegistrationService registrationService;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

        User user = customUser.getUser();

        ProfileResponse response = ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .documentType(user.getDocumentType())
                .documentNumber(user.getDocumentNumber())
                .birthDate(user.getBirthDate())
                .state(user.getState())
                .city(user.getCity())
                .address(user.getAddress())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .active(user.getActive())
                .role(
                        user.getRole() != null
                                ? user.getRole().getName()
                                : "ROLE_CLIENT")
                .profileCompleted(user.getProfileCompleted())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/complete")
    public ResponseEntity<Map<String, String>> completeProfile(Authentication authentication,
            @Valid @RequestBody CompleteProfileRequest request) {
        if (authentication == null)
            return ResponseEntity.status(401).build();

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
        User user = customUser.getUser();

        registrationService.completeProfile(user.getEmail(), request);

        return ResponseEntity.ok(Map.of("message", "Perfil completado correctamente"));
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody CompleteProfileRequest request) {

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

        User user = customUser.getUser();

        ProfileResponse response = profileService.updateProfile(
                user.getEmail(),
                request);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminTest() {
        return ResponseEntity.ok(Map.of("message", "Acceso autorizado para ADMIN"));
    }
}