package com.hernandolopera.auth_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.admin.AssignRoleRequest;
import com.hernandolopera.auth_service.security.details.CustomUserDetails;
import com.hernandolopera.auth_service.service.auth.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RoleService roleService;

    // 🔹 Perfil del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario no autenticado"));
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails user)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error en el contexto de seguridad"));
        }

        return ResponseEntity.ok(Map.of(
                "email", user.getUsername(),
                "role", user.getUser().getRole().getName(),
                "profileCompleted", user.isProfileCompleted(),
                "permissions", user.getAuthorities()
                        .stream()
                        .map(auth -> auth.getAuthority())
                        .toList()));
    }

    // 🔥 ASIGNAR ROL A USUARIO (IMPORTANTE)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/role")
    public ResponseEntity<Map<String, String>> assignRole(
            @PathVariable Integer id,
            @RequestBody AssignRoleRequest request) {

        request.setUserId(id);
        roleService.assignRole(request);

        return ResponseEntity.ok(Map.of(
                "message", "Rol asignado correctamente"));
    }

    // 🔹 Test ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-test")
    public ResponseEntity<Map<String, String>> adminTest() {
        return ResponseEntity.ok(Map.of("message", "Acceso autorizado para ADMIN"));
    }

    // 🔹 Test USER
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @GetMapping("/user-test")
    public ResponseEntity<Map<String, String>> userTest() {
        return ResponseEntity.ok(Map.of("message", "Acceso autorizado para usuario autenticado"));
    }
}