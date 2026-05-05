package com.hernandolopera.auth_service.dto.request.admin;

import lombok.Data;

@Data
public class AssignRoleRequest {
    private Integer userId;
    private String roleName;
}
