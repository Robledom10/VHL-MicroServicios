package com.hernandolopera.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
