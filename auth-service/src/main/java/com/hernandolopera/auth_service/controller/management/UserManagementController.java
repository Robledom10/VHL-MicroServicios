package com.hernandolopera.auth_service.controller.management;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.response.UserResponse;
import com.hernandolopera.auth_service.service.auth.UserManagementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    private final UserManagementService userManagementService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userManagementService.getUserById(id);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Integer id) {
        UserResponse response = userManagementService.disableUser(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable Integer id) {
        UserResponse response = userManagementService.enableUser(id);
        return ResponseEntity.ok(response);
    }
}
