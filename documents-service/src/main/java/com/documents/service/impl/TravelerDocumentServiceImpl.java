package com.documents.service.impl;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.DocumentType;
import com.documents.repository.DocumentValidationRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelerDocumentServiceImpl
        implements TravelerDocumentService {

    private final TravelerDocumentRepository repository;

    private final DocumentValidationRepository validationRepository;

    private final String uploadDir = "uploads/";

    @Override
    public TravelerDocument uploadDocument(
            Integer userId,
            String documentType,
            MultipartFile file
    ) {

        if (userId == null) {
            throw new IllegalArgumentException("El userId es obligatorio");
        }

        DocumentType parsedDocumentType = parseDocumentType(documentType);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacio");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || (!contentType.equals("application/pdf")
                && !contentType.equals("image/png")
                && !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Solo PDF, PNG o JPG");
        }

        String originalFilename = Optional.ofNullable(file.getOriginalFilename())
                .map(StringUtils::cleanPath)
                .filter(name -> !name.isBlank())
                .orElse("documento");

        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Nombre de archivo invalido");
        }

        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "No se pudo guardar el archivo",
                    ex
            );
        }

        TravelerDocument document = TravelerDocument.builder()
                .userId(userId)
                .documentType(parsedDocumentType)
                .fileUrl(filePath.toString())
                .status(DocumentStatus.pendiente)
                .build();

        return repository.save(document);
    }

    @Override
    public List<TravelerDocument> getAllDocuments() {

        return repository.findAll();
    }

    @Override
    public List<TravelerDocument> getUserDocuments(
            Integer userId
    ) {

        return repository.findByUserId(userId);
    }

    @Override
    public List<TravelerDocument> getDocumentsByStatus(
            DocumentStatus status
    ) {

        return repository.findByStatus(status);
    }

    @Override
    public TravelerDocument getDocument(
            Integer documentId
    ) {

        return repository.findById(documentId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Documento no encontrado"
                        )
                );
    }

    @Override
    public Resource downloadDocument(
            Integer documentId
    ) {

        TravelerDocument document = getDocument(documentId);
        Path filePath = Paths.get(document.getFileUrl()).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException(
                        "El archivo no existe o no se puede leer"
                );
            }

            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(
                    "Ruta de archivo invalida",
                    ex
            );
        }
    }

    @Override
    @Transactional
    public void deleteDocument(
            Integer documentId
    ) {

        TravelerDocument document = getDocument(documentId);

        validationRepository.deleteByTravelerDocumentIdDocument(documentId);

        if (document.getFileUrl() != null && !document.getFileUrl().isBlank()) {
            try {
                Files.deleteIfExists(Paths.get(document.getFileUrl()).normalize());
            } catch (IOException ex) {
                throw new IllegalStateException(
                        "No se pudo eliminar el archivo del documento",
                        ex
                );
            }
        }

        repository.delete(document);
    }

    private DocumentType parseDocumentType(String documentType) {
        if (documentType == null || documentType.isBlank()) {
            throw new IllegalArgumentException(
                    "El documentType es obligatorio"
            );
        }

        String normalizedDocumentType = documentType.trim();

        return Arrays.stream(DocumentType.values())
                .filter(type -> type.name().equalsIgnoreCase(
                        normalizedDocumentType
                ))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Tipo de documento invalido. Valores permitidos: "
                                        + Arrays.toString(DocumentType.values())
                        )
                );
    }
}
