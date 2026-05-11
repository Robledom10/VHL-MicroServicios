package com.hernandolopera.auth_service.dto.response;

import java.time.LocalDate;
import java.util.Set;

import com.hernandolopera.auth_service.enums.DocumentType;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;

    private DocumentType documentType;
    private String documentNumber;

    private LocalDate birthDate;

    private String state;
    private String city;
    private String address;

    private Boolean active;

    private Boolean profileCompleted;

    private Set<String> roles;
}
