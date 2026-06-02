package com.documents.controller;

import com.documents.entity.TravelerDocument;
import com.documents.repository.TravelerDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
public class AdminValidationController {

    private final TravelerDocumentRepository repository;

    @GetMapping
    public List<TravelerDocument> getAll() {

        return repository.findAll();
    }
}