package com.hernandolopera.auth_service.controller.management;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.request.admin.AssignRoleRequest;
import com.hernandolopera.auth_service.dto.request.admin.RoleRequest;
import com.hernandolopera.auth_service.dto.response.RoleResponse;
import com.hernandolopera.auth_service.service.auth.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔥 lo subimos a nivel de clase
public class RoleController {

    private final RoleService roleService;

    // 🔹 Obtener todos los roles
    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    // 🔹 Obtener rol por ID
    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id);
    }

    // 🔹 Crear rol
    @PostMapping
    public RoleResponse createRole(@Valid @RequestBody RoleRequest request) {
        return roleService.map(roleService.createRole(request));
    }

    // 🔹 Actualizar rol
    @PutMapping("/{id}")
    public RoleResponse updateRole(@PathVariable Integer id,
            @RequestBody RoleRequest request) {
        return roleService.map(roleService.updateRole(id, request));
    }

    // 🔹 Eliminar rol
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
    }

    // 🔹 Asignar rol a usuario
    @PostMapping("/users/{userId}")
    public void assignRoleToUser(@PathVariable Integer userId,
            @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(userId, request);
    }
}