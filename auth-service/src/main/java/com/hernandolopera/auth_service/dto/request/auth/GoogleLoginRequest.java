package com.hernandolopera.auth_service.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    @JsonProperty("idToken")
    private String idToken;
}
