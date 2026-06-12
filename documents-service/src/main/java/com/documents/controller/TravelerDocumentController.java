package com.documents.controller;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            ) {

        return ResponseEntity.ok(
                service.uploadDocument(
                        userId,
                        documentType,
                        file
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<TravelerDocument>>
            getAllDocuments() {

        return ResponseEntity.ok(
                service.getAllDocuments()
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TravelerDocument>>
            getUserDocumentsByUserPath(
                    @PathVariable Integer userId
            ) {

        return ResponseEntity.ok(
                service.getUserDocuments(userId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TravelerDocument>>
            documentsByStatus(
                    @PathVariable DocumentStatus status
            ) {

        return ResponseEntity.ok(
                service.getDocumentsByStatus(
                        status
                )
        );
    }

    @GetMapping("/detail/{documentId}")
    public ResponseEntity<TravelerDocument>
            documentDetail(
                    @PathVariable Integer documentId
            ) {

        return ResponseEntity.ok(
                service.getDocument(
                        documentId
                )
        );
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource>
            downloadDocument(
                    @PathVariable Integer documentId
            ) {

        Resource resource = service.downloadDocument(documentId);
        String filename = resource.getFilename() == null
                ? "documento"
                : resource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void>
            deleteDocument(
                    @PathVariable Integer documentId
            ) {

        service.deleteDocument(documentId);

        return ResponseEntity.noContent()
                .build();
    }
}
