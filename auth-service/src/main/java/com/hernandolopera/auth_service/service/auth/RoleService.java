package com.hernandolopera.auth_service.service.auth;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hernandolopera.auth_service.dto.request.admin.AssignRoleRequest;
import com.hernandolopera.auth_service.dto.request.admin.RoleRequest;
import com.hernandolopera.auth_service.dto.response.RoleResponse;
import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User;
import com.hernandolopera.auth_service.repository.auth.RoleRepository;
import com.hernandolopera.auth_service.repository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // 🔹 Obtener todos los roles
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    // 🔹 Obtener rol por ID
    public RoleResponse getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        return map(role);
    }

    // 🔥 Crear rol
    @Transactional
    public Role createRole(RoleRequest request) {

        String roleName = normalize(request.getName());

        if (roleRepository.findByName(roleName).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rol ya existe");
        }

        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }

    // 🔥 Actualizar rol
    @Transactional
    public Role updateRole(Integer roleId, RoleRequest request) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        String roleName = normalize(request.getName());

        if (roleRepository.findByName(roleName)
                .filter(r -> !r.getId().equals(roleId))
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro rol con ese nombre");
        }

        role.setName(roleName);

        return roleRepository.save(role);
    }

    // 🔥 Eliminar rol
    public void deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        // Usamos el repositorio de usuarios para contar cuántos tienen este rol
        long userCount = userRepository.countByRoles(role);

        if (userCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar un rol asignado a " + userCount + " usuarios");
        }

        roleRepository.delete(role);
    }

    // 🔥 Asignar rol a usuario
    // 🔥 Asignar rol a usuario (CORREGIDO PARA OBJETO ÚNICO)
    @Transactional
    public void assignRoleToUser(Integer userId, AssignRoleRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Role newRole = roleRepository.findByName(normalize(request.getRoleName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        // 1. Ya no usamos .stream(), accedemos directamente al nombre
        boolean isClient = user.getRoles().getName().equals("ROLE_CLIENT");

        boolean isPrivilegedRole = newRole.getName().equals("ROLE_ADMIN")
                || newRole.getName().equals("ROLE_GUIDE");

        if (isClient && isPrivilegedRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un cliente no puede ser promovido sin validación administrativa");
        }

        // 2. Comparación directa de objetos
        if (user.getRoles().equals(newRole)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya tiene este rol");
        }

        // 3. Ya no usamos .add(), usamos el setter del objeto
        user.setRoles(newRole);
        userRepository.save(user);
    }

    // 🔧 Normalizar nombres
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre inválido");
        }
        return value.trim().toUpperCase();
    }

    // 🔹 Mapper DTO
    public RoleResponse map(Role role) {
        RoleResponse dto = new RoleResponse();

        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setStatus(role.getStatus());

        return dto;
    }
}