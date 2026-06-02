package com.documents.controller;

import com.documents.entity.DocumentValidation;
import com.documents.repository.DocumentValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ValidationHistoryController {

    private final DocumentValidationRepository repository;

    @GetMapping("/{documentId}")
    public List<DocumentValidation>
    history(
            @PathVariable Integer documentId
    ){

        return repository
                .findByTravelerDocumentIdDocument(
                        documentId
                );
    }
}
