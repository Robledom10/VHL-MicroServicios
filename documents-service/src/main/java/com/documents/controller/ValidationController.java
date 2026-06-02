package com.documents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.documents.service.ValidationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService service;

    @PostMapping("/{documentId}")
    public ResponseEntity<String> validateDocument(
            @PathVariable Integer documentId
    ) {

        service.validateDocument(documentId);

        return ResponseEntity.ok(
                "Documento validado"
        );
    }
}
