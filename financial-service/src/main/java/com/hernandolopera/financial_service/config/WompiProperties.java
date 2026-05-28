package com.hernandolopera.financial_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "wompi")
@Getter @Setter
public class WompiProperties {
    private String baseUrl;
    private String publicKey;
    private String privateKey;
    private String integritySecret;
    private String redirectUrl;
}
