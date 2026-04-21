package com.hernandolopera.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.auth_service.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

}
