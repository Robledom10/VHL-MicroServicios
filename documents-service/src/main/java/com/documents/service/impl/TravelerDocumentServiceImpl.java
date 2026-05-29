package com.documents.service.impl;
import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.DocumentType;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelerDocumentServiceImpl
        implements TravelerDocumentService {

    private final TravelerDocumentRepository repository;

    private final String UPLOAD_DIR = "uploads/";

    @Override
    public TravelerDocument uploadDocument(
            Integer userId,
            String documentType,
            MultipartFile file
    ) throws IOException {

        // VALIDAR ARCHIVO
        if (file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }

        // VALIDAR TIPO
        String contentType = file.getContentType();

        if (!contentType.equals("application/pdf")
                && !contentType.equals("image/png")
                && !contentType.equals("image/jpeg")) {

            throw new RuntimeException(
                    "Solo PDF, PNG o JPG"
            );
        }

        // CREAR CARPETA
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // NOMBRE ARCHIVO
        String fileName = System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        // GUARDAR ARCHIVO
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // GUARDAR BD
        TravelerDocument document =
                TravelerDocument.builder()
                        .userId(userId)
                        .documentType(
                                DocumentType.valueOf(
                                        documentType
                                )
                        )
                        .fileUrl(filePath.toString())
                        .status(DocumentStatus.pendiente)
                        .build();

        return repository.save(document);
    }

    @Override
    public List<TravelerDocument> getUserDocuments(
            Integer userId
    ) {

        return repository.findByUserId(userId);
    }
}