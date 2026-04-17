package com.hernandolopera.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.auth_service.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findbyName(String name);
}
