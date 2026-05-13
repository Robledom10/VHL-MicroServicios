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
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import com.hernandolopera.auth_service.service.auth.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    // 🔹 Listar roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // 🔹 Crear rol
    @PostMapping
    public Role createRole(@RequestBody RoleRequest request) {
        return roleService.createRole(request);
    }

    // 🔹 Actualizar rol
    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Integer id,
                           @RequestBody RoleRequest request) {
        return roleService.updateRole(id, request);
    }

    // 🔹 Eliminar rol
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
    }

    // 🔹 Asignar rol a usuario
    @PostMapping("/assign")
    public void assignRole(@RequestBody AssignRoleRequest request) {
        roleService.assignRole(request);
    }
}