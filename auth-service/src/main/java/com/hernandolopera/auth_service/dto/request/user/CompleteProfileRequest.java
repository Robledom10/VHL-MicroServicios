package com.hernandolopera.auth_service.dto.request.user;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteProfileRequest {

    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    // @NotNull(message = "El tipo de documento es obligatorio")
    // private DocumentType documentType;

    // @NotBlank(message = "El número de documento es obligatorio")
    // private String documentNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    @NotBlank(message = "El departamento es obligatorio")
    private String state;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;
}