package com.documents.service.impl;

import org.springframework.stereotype.Service;

import com.documents.dto.IAResponseDTO;
import com.documents.entity.DocumentValidation;
import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.ValidationResult;
import com.documents.entity.enums.ValidationSource;
import com.documents.repository.DocumentValidationRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.IAService;
import com.documents.service.ValidationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl
        implements ValidationService {

    private final TravelerDocumentRepository documentRepository;

    private final DocumentValidationRepository validationRepository;

    private final IAService iaService;

    @Override
    public void validateDocument(
            Integer documentId
    ) {

        TravelerDocument document =
                documentRepository.findById(documentId)
                        .orElseThrow();

        IAResponseDTO response =
                iaService.analyzeDocument(document);

        DocumentValidation validation =
                DocumentValidation.builder()
                        .travelerDocument(document)
                        .source(ValidationSource.IA)
                        .result(
                                response.getValid()
                                ?
                                ValidationResult.valido
                                :
                                ValidationResult.invalido
                        )
                        .observations(
                                response.getObservations()
                        )
                        .build();

        validationRepository.save(validation);

        document.setStatus(
                response.getValid()
                ?
                DocumentStatus.aprobado
                :
                DocumentStatus.rechazado
        );

        documentRepository.save(document);
    }
}
