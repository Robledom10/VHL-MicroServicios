package com.hernandolopera.auth_service.dto.request.admin;

import java.util.Set;

import lombok.Data;

@Data
public class RoleRequest {
    private String name;
    private Set<String> permissions; // nombres de permisos
}