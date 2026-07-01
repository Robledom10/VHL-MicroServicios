package com.hernandolopera.auth_service.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
