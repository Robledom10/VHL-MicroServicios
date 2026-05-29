package com.hernandolopera.auth_service.dto.request.auth;

import lombok.Data;

@Data
public class GoogleLoginRequest {

    private String idToken;
}
