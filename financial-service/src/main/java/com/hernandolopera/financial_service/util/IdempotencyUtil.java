package com.hernandolopera.financial_service.util;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class IdempotencyUtil {

    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
