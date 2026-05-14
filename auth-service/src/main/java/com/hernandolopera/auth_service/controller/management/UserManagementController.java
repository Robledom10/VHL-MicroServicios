package com.hernandolopera.auth_service.controller.management;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.response.UserResponse;
import com.hernandolopera.auth_service.service.managment.UserManagementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userManagementService.getAllUsers());
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                userManagementService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/disable")
    public ResponseEntity<Map<String, String>> disableUser(
            @PathVariable Integer id) {

        userManagementService.disableUser(id);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Usuario deshabilitado correctamente"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/enable")
    public ResponseEntity<Map<String, String>> enableUser(
            @PathVariable Integer id) {

        userManagementService.enableUser(id);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Usuario habilitado correctamente"));
    }
}