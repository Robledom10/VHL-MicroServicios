package com.documents.controller;
import com.documents.entity.TravelerDocument;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class TravelerDocumentController {

    private final TravelerDocumentService service;

    @PostMapping("/upload")
    public ResponseEntity<TravelerDocument>
    uploadDocument(

            @RequestParam Integer userId,

            @RequestParam String documentType,

            @RequestParam MultipartFile file

    ) throws IOException {

        return ResponseEntity.ok(
                service.uploadDocument(
                        userId,
                        documentType,
                        file
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TravelerDocument>>
    getUserDocuments(
            @PathVariable Integer userId
    ) {

        return ResponseEntity.ok(
                service.getUserDocuments(userId)
        );
    }
}