package com.hernandolopera.auth_service.controller.management;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.security.details.CustomUserDetails;
import com.hernandolopera.auth_service.service.auth.RoleService;
import com.hernandolopera.auth_service.service.auth.RegistrationService;
import com.hernandolopera.auth_service.dto.request.user.CompleteProfileRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final RoleService roleService;
    private final RegistrationService registrationService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(401).build();

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
        User user = customUser.getUser();

        // 🛡️ Construimos un JSON plano y seguro
        Map<String, Object> body = new HashMap<>();
        body.put("email", user.getEmail());
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());
        body.put("role", user.getRole() != null ? user.getRole().getName() : "USER");
        body.put("profileCompleted", user.getProfileCompleted());

        // Solo enviamos los nombres de los permisos como Strings
        body.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return ResponseEntity.ok(body);
    }

    @PutMapping("/complete-profile")
    public ResponseEntity<Map<String, String>> completeProfile(Authentication authentication, @Valid @RequestBody CompleteProfileRequest request) {
        if (authentication == null)
            return ResponseEntity.status(401).build();

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
        User user = customUser.getUser();
        
        registrationService.completeProfile(user.getEmail(), request);
        
        return ResponseEntity.ok(Map.of("message", "Perfil completado correctamente"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminTest() {
        return ResponseEntity.ok(Map.of("message", "Acceso autorizado para ADMIN"));
    }
}