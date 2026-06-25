package com.hernandolopera.auth_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DocumentType fromValue(String text) {
        for (DocumentType dt : DocumentType.values()) {
            if (dt.value.equalsIgnoreCase(text) || dt.name().equalsIgnoreCase(text)) {
                return dt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
