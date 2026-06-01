package com.hernandolopera.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta estandar para autenticación.
 * Devuelve access token + refresh token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserLoginResponse user;

    /**
     * ! Tener en cuenta para el futuro 
        //?private boolean isNewUser;
    */
}
