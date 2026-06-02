package com.documents.controller;

import com.documents.entity.TravelerDocument;
import com.documents.repository.TravelerDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class DocumentValidationController {

    private final TravelerDocumentRepository repository;

    @GetMapping("/detail/{documentId}")
    public TravelerDocument detail(
            @PathVariable Integer documentId
    ) {

        return repository
                .findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Documento no encontrado"
                        )
                );
    }
}