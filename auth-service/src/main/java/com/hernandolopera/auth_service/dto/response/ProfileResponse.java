package com.hernandolopera.auth_service.dto.response;

import java.time.LocalDate;

import com.hernandolopera.auth_service.enums.DocumentType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {

    private Integer id;

    private String firstName;
    private String lastName;

    private String email;
    private String phone;

    private DocumentType documentType;
    private String documentNumber;

    private LocalDate birthDate;

    private String state;
    private String city;
    private String address;

    private Boolean emailVerified;
    private Boolean phoneVerified;

    private Boolean active;

    private String role;

    private Boolean profileCompleted;
}
