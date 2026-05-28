package com.hernandolopera.financial_service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Component;

import com.hernandolopera.financial_service.config.WompiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WompiSignatureUtil {

    private final WompiProperties properties;

    public String generate(String reference, String amount, String currency) {
        String raw = reference + amount + currency + properties.getIntegritySecret();
        return sha256(raw);
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
