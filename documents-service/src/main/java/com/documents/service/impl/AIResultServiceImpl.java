package com.documents.service.impl;

import com.documents.dto.AIValidationResultDTO;
import com.documents.entity.DocumentValidation;
import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.ValidationResult;
import com.documents.entity.enums.ValidationSource;
import com.documents.repository.DocumentValidationRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.AIResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AIResultServiceImpl
        implements AIResultService {

    private final TravelerDocumentRepository documentRepository;

    private final DocumentValidationRepository validationRepository;

   @Override
public void processResult(
        AIValidationResultDTO dto
) {

    // Validaciones de entrada

    if(dto.getDocumentId() == null){
        throw new RuntimeException(
                "DocumentId requerido"
        );
    }

    if(dto.getValid() == null){
        throw new RuntimeException(
                "Resultado requerido"
        );
    }

    // Buscar documento

    TravelerDocument document =
            documentRepository
                    .findById(dto.getDocumentId())
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Documento no encontrado"
                            )
                    );

    // Crear validación

    DocumentValidation validation =
            DocumentValidation.builder()
                    .travelerDocument(document)
                    .validationDate(LocalDateTime.now())
                    .source(ValidationSource.IA)
                    .result(
                            dto.getValid()
                            ?
                            ValidationResult.valido
                            :
                            ValidationResult.invalido
                    )
                    .observations(
                            dto.getObservations()
                    )
                    .build();

    validationRepository.save(validation);

    // Actualizar estado del documento

    document.setStatus(
            dto.getValid()
            ?
            DocumentStatus.aprobado
            :
            DocumentStatus.rechazado
    );

    documentRepository.save(document);
    }
}
