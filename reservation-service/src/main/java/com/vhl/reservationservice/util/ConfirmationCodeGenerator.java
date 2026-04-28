package com.vhl.reservationservice.util;

import java.security.SecureRandom;
import java.util.UUID;

public class ConfirmationCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Genera un código de confirmación único para una reserva
     * Formato: RES-XXXXXX-YYYY (donde X es alfanumérico y YYYY es el año)
     */
    public static String generate() {
        StringBuilder code = new StringBuilder("RES-");
        
        // Agregar 6 caracteres aleatorios
        for (int i = 0; i < 6; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        // Agregar separador y año
        code.append("-").append(java.time.Year.now().getValue());
        
        return code.toString();
    }

    /**
     * Genera un UUID único para la reserva
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Valida si el formato del código de confirmación es válido
     */
    public static boolean isValidConfirmationCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return code.matches("RES-[A-Z0-9]{6}-\\d{4}");
    }
}
