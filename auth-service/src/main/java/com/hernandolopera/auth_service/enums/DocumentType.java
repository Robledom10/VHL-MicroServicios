package com.hernandolopera.auth_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    CEDULA_CIUDADANIA("Cedula Ciudadania"),
    TARJETA_IDENTIDAD("Tarjeta Identidad"),
    PASAPORTE("Pasaporte"),
    CEDULA_EXTRANJERIA("Cedula Extranjeria"),
    VISA("Visa");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    @JsonValue // Para que Jackson use el String en el JSON
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
