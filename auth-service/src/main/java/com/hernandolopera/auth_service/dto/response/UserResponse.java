package com.hernandolopera.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String documentType;

    private String documentNumber;

    private String role;

    private Boolean active;

    private Boolean profileCompleted;
}