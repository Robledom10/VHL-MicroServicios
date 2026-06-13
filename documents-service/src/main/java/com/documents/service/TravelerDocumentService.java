package com.documents.service;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;

public interface TravelerDocumentService {

    TravelerDocument uploadDocument(
            Integer userId,
            String documentType,
            MultipartFile file
    );

    List<TravelerDocument> getAllDocuments();

    List<TravelerDocument> getUserDocuments(
            Integer userId
    );

    List<TravelerDocument> getDocumentsByStatus(
        DocumentStatus status
        );

    TravelerDocument getDocument(
        Integer documentId
        );

    Resource downloadDocument(
            Integer documentId
    );

    void deleteDocument(
            Integer documentId
    );
}
