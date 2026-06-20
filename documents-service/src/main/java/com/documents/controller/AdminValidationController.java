package com.documents.controller;

import com.documents.dto.AdminDocumentValidationDTO;
import com.documents.entity.TravelerDocument;
import com.documents.service.AdminValidationService;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
public class AdminValidationController {

    private final TravelerDocumentService documentService;

    private final AdminValidationService adminValidationService;

    @GetMapping
    public ResponseEntity<List<TravelerDocument>> getAll() {

        return ResponseEntity.ok(
                documentService.getAllDocuments()
        );
    }

    @PostMapping("/{documentId}/approve")
    public ResponseEntity<TravelerDocument> approveDocument(
            @PathVariable Integer documentId,
            @RequestBody(required = false) AdminDocumentValidationDTO dto
    ) {

        return ResponseEntity.ok(
                adminValidationService.approveDocument(
                        documentId,
                        dto
                )
        );
    }

    @PostMapping("/{documentId}/reject")
    public ResponseEntity<TravelerDocument> rejectDocument(
            @PathVariable Integer documentId,
            @RequestBody(required = false) AdminDocumentValidationDTO dto
    ) {

        return ResponseEntity.ok(
                adminValidationService.rejectDocument(
                        documentId,
                        dto
                )
        );
    }

    @PostMapping("/{documentId}/in-process")
    public ResponseEntity<TravelerDocument> markInProcess(
            @PathVariable Integer documentId,
            @RequestBody(required = false) AdminDocumentValidationDTO dto
    ) {

        return ResponseEntity.ok(
                adminValidationService.markInProcess(
                        documentId,
                        dto
                )
        );
    }
}
