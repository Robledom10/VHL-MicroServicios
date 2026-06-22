package com.documents.controller;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                    @RequestParam(required = false) Integer reservationId,
                    @RequestParam String documentType,
                    @RequestParam MultipartFile file
            ) {

        return ResponseEntity.ok(
                service.uploadDocument(
                        userId,
                        reservationId,
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
        String filename = getFilename(resource);

        return ResponseEntity.ok()
                .contentType(getMediaType(resource))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        filename,
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource>
            viewDocument(
                    @PathVariable Integer documentId
            ) {

        Resource resource = service.downloadDocument(documentId);
        String filename = getFilename(resource);

        return ResponseEntity.ok()
                .contentType(getMediaType(resource))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(
                                        filename,
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
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

    private String getFilename(
            Resource resource
    ) {

        return resource.getFilename() == null
                ? "documento"
                : resource.getFilename();
    }

    private MediaType getMediaType(
            Resource resource
    ) {

        try {
            String contentType =
                    Files.probeContentType(resource.getFile().toPath());

            if (contentType != null) {
                return MediaType.parseMediaType(contentType);
            }
        } catch (IOException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
