package com.hernandolopera.auth_service.service.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.request.admin.AssignRoleRequest;
import com.hernandolopera.auth_service.dto.request.admin.RoleRequest;
import com.hernandolopera.auth_service.entity.auth.Permission;
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.PermissionRepository;
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    // 🔥 Crear rol
    @Transactional
    public Role createRole(RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rol ya existe");
        }

        Role role = new Role();
        role.setName(request.getName());

        role.setPermissions(getPermissions(request.getPermissions()));
        return roleRepository.save(role);
    }

    // 🔥 Actualizar rol
    @Transactional
    public Role updateRole(Integer roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        role.setName(request.getName());

        role.setPermissions(getPermissions(request.getPermissions()));
        return roleRepository.save(role);
    }

    // 🔥 Eliminar rol
    public void deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        roleRepository.delete(role);
    }

    // 🔥 Asignar rol a usuario
    @Transactional
    public void assignRoleToUser(Integer userId, AssignRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Role role = roleRepository.findByName(normalize(request.getRoleName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        user.setRole(role);
        userRepository.save(user);
    }

    // 🔧 Helper: obtener permisos
    private Set<Permission> getPermissions(Set<String> permissionNames) {

        if (permissionNames == null || permissionNames.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar al menos un permiso");
        }

        Set<Permission> permissions = new HashSet<>();

        for (String permName : permissionNames) {
            Permission permission = permissionRepository.findByName(normalize(permName))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Permiso no encontrado: " + permName));

            permissions.add(permission);
        }

        return permissions;
    }

    // 🔧 Helper: normalizar nombres
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre inválido");
        }
        return value.trim().toUpperCase();
    }

}