package com.hernandolopera.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;
}
