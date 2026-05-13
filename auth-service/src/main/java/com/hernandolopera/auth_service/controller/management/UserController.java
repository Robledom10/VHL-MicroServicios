package com.hernandolopera.auth_service.controller.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.PostMapping;
=======
import org.springframework.web.bind.annotation.PutMapping;
>>>>>>> origin/develop
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.security.details.CustomUserDetails;
import com.hernandolopera.auth_service.service.auth.RegistrationService;
import com.hernandolopera.auth_service.service.auth.RoleService;
import com.hernandolopera.auth_service.service.auth.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final RoleService roleService;
<<<<<<< HEAD
    private final RegistrationService registrationService;
=======
    private final UserService userService;
>>>>>>> origin/develop

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        if (authentication == null)
            return ResponseEntity.status(401).build();

        CustomUserDetails customUser =
                (CustomUserDetails) authentication.getPrincipal();

        User user = customUser.getUser();

        Map<String, Object> body = new HashMap<>();

        body.put("email", user.getEmail());
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());

        body.put(
                "roles",
                user.getRoles() != null
                        ? List.of(user.getRoles().getName())
                        : List.of("ROLE_USER"));

        body.put("profileCompleted", user.getProfileCompleted());

        body.put(
                "authorities",
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

        return ResponseEntity.ok(body);
    }

<<<<<<< HEAD
    @PostMapping("/complete-profile")
    public ResponseEntity<Map<String, Object>> completeProfile(
            Authentication authentication,
            @RequestBody CompleteProfileRequest request) {

        if (authentication == null)
            return ResponseEntity.status(401).build();

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
        registrationService.completeProfile(customUser.getUsername(), request);

        return ResponseEntity.ok(Map.of(
                "message", "Perfil completado correctamente",
                "profileCompleted", true));
=======
    /**
     * Completar perfil del usuario autenticado
     */
    @PutMapping("/complete-profile")
    public ResponseEntity<Map<String, Object>> completeProfile(
            @Valid @RequestBody CompleteProfileRequest request,
            Authentication authentication) {

        CustomUserDetails customUser =
                (CustomUserDetails) authentication.getPrincipal();

        userService.completeProfile(
                customUser.getUsername(),
                request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Perfil completado correctamente",
                        "status", 200));
>>>>>>> origin/develop
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminTest() {

        return ResponseEntity.ok(
                Map.of(
                        "message", "Acceso autorizado para ADMIN"));
    }
}
