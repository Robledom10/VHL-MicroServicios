package com.hernandolopera.auth_service.dto.request.user;

import java.time.LocalDate;
import com.hernandolopera.auth_service.enums.DocumentType;
import lombok.Data;

@Data
public class CompleteProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birthDate;
    private String state;
    private String city;
    private String address;
    private DocumentType documentType;
    private String documentNumber;
    private String email; // Por si permites cambiar el correo (ojo con las validaciones de duplicados)
}