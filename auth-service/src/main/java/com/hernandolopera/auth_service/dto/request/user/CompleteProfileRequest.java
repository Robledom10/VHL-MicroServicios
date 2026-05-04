package com.hernandolopera.auth_service.dto.request.user;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CompleteProfileRequest {
    private String phone;
    private LocalDate birthDate;
    private String state;
    private String city;
    private String address;
}