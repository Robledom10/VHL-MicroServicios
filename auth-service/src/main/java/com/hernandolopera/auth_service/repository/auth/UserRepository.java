package com.hernandolopera.auth_service.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.auth_service.entity.auth.Role;
import com.hernandolopera.auth_service.entity.auth.User; // No olvides este import

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    // Si en User.java pusiste "private Role roles", aquí debe ser "Roles"
    long countByRoles(Role role);
}