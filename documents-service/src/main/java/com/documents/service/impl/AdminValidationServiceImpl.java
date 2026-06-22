package com.documents.service.impl;

import com.documents.dto.AdminDocumentValidationDTO;
import com.documents.entity.DocumentValidation;
import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.ValidationResult;
import com.documents.entity.enums.ValidationSource;
import com.documents.repository.DocumentValidationRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.AdminValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminValidationServiceImpl
        implements AdminValidationService {

    private final TravelerDocumentRepository documentRepository;

    private final DocumentValidationRepository validationRepository;

    @Override
    public TravelerDocument approveDocument(
            Integer documentId,
            AdminDocumentValidationDTO dto
    ) {

        return updateDocumentStatus(
                documentId,
                DocumentStatus.aprobado,
                ValidationResult.valido,
                dto,
                false
        );
    }

    @Override
    public TravelerDocument rejectDocument(
            Integer documentId,
            AdminDocumentValidationDTO dto
    ) {

        return updateDocumentStatus(
                documentId,
                DocumentStatus.rechazado,
                ValidationResult.invalido,
                dto,
                true
        );
    }

    @Override
    public TravelerDocument markInProcess(
            Integer documentId,
            AdminDocumentValidationDTO dto
    ) {

        return updateDocumentStatus(
                documentId,
                DocumentStatus.en_proceso,
                ValidationResult.pendiente,
                dto,
                false
        );
    }

    private TravelerDocument updateDocumentStatus(
            Integer documentId,
            DocumentStatus status,
            ValidationResult result,
            AdminDocumentValidationDTO dto,
            boolean requireObservations
    ) {

        TravelerDocument document =
                documentRepository.findById(documentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Documento no encontrado"
                                )
                        );

        DocumentValidation validation =
                DocumentValidation.builder()
                        .travelerDocument(document)
                        .validationDate(LocalDateTime.now())
                        .source(ValidationSource.admin)
                        .result(result)
                        .observations(
                                getObservations(
                                        dto,
                                        requireObservations
                                )
                        )
                        .build();

        validationRepository.save(validation);

        document.setStatus(status);

        return documentRepository.save(document);
    }

    private String getObservations(
            AdminDocumentValidationDTO dto,
            boolean required
    ) {

        if (dto == null || dto.getObservations() == null) {
            if (required) {
                throw new IllegalArgumentException(
                        "El motivo de rechazo es obligatorio"
                );
            }

            return null;
        }

        String observations = dto.getObservations().trim();

        if (required && observations.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo de rechazo es obligatorio"
            );
        }

        return observations.isBlank()
                ? null
                : observations;
    }
}
