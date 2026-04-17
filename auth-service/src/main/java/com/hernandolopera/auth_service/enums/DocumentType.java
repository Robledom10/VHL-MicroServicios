package com.hernandolopera.auth_service.enums;

public enum DocumentType {
    CEDULA_CIUDADANIA("Cedula Cidadania"),
    TARJETA_IDENTIDAD("Tarjeta Identidad"),
    PASAPORTE("Pasaporte"),
    CEDULA_EXTRANJERIA("Cedula Extranjeria"),
    VISA("Visa");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
