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
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.service.auth.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 🔹 Obtener todos los roles
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // 🔹 Obtener rol por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Role getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id);
    }

    // 🔹 Crear rol
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Role createRole(@RequestBody RoleRequest request) {
        return roleService.createRole(request);
    }

    // 🔹 Actualizar rol
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Role updateRole(@PathVariable Integer id,
            @RequestBody RoleRequest request) {
        return roleService.updateRole(id, request);
    }

    // 🔹 Eliminar rol
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
    }

    // 🔹 Asignar rol a usuario (multi-rol)
    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRoleToUser(@PathVariable Integer userId,
            @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(userId, request);
    }
}