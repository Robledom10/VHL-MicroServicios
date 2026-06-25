package com.hernandolopera.auth_service.config;

import com.hernandolopera.auth_service.enums.DocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentType documentType) {
        return documentType != null ? documentType.getValue() : null;
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        for (DocumentType dt : DocumentType.values()) {
            if (dt.getValue().equals(dbData) || dt.name().equals(dbData)) {
                return dt;
            }
        }
        return null;
    }
}
