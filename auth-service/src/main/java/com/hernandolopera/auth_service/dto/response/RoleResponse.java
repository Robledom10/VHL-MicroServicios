package com.hernandolopera.auth_service.dto.response;

import java.util.Set;

import lombok.Data;

@Data
public class RoleResponse {
    private Integer id;
    private String name;
    private Boolean status;
    private Set<String> permissions;
}
