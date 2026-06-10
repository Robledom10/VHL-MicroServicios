package com.documents.service.impl;
//Implementación simulada IA
import org.springframework.stereotype.Service;

import com.documents.dto.IAResponseDTO;
import com.documents.entity.TravelerDocument;
import com.documents.service.IAService;

@Service
public class IAServiceImpl implements IAService {

    @Override
    public IAResponseDTO analyzeDocument(
            TravelerDocument document
    ) {

        return IAResponseDTO.builder()
                .valid(true)
                .observations(
                        "Documento válido"
                )
                .build();
    }
}