package com.hernandolopera.auth_service.dto.request;

import java.util.Set;

import com.hernandolopera.auth_service.enums.DocumentType;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private DocumentType documentType;
    private String documentNumber;
    private Set<String> role;
}
