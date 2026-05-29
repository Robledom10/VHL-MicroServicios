package com.documents.service;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.documents.entity.TravelerDocument;

public interface TravelerDocumentService {

    TravelerDocument uploadDocument(
            Integer userId,
            String documentType,
            MultipartFile file
    ) throws IOException;

    List<TravelerDocument> getUserDocuments(
            Integer userId
    );
}