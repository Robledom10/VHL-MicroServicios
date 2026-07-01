package com.documents.service.impl;

import com.documents.dto.ReservationSummaryDTO;
import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.DocumentType;
import com.documents.repository.DocumentValidationRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.TravelerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelerDocumentServiceImpl
        implements TravelerDocumentService {

    private final TravelerDocumentRepository repository;

    private final DocumentValidationRepository validationRepository;

    private final String uploadDir = "uploads/";

    @Value("${reservation.service.url:http://reservation-service:8082}")
    private String reservationServiceUrl;

    @Override
    public TravelerDocument uploadDocument(
            Integer userId,
            Integer reservationId,
            MultipartFile file
    ) {

        if (userId == null) {
            throw new IllegalArgumentException("El userId es obligatorio");
        }

        ResolvedContext context = resolveContext(userId, reservationId);

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

        DocumentType parsedDocumentType =
                inferDocumentTypeFromFilename(originalFilename);

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
                .reservationId(context.reservationId())
                .packageId(context.packageId())
                .tripId(context.tripId())
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

    private ResolvedContext resolveContext(
            Integer userId,
            Integer reservationId
    ) {

        RestTemplate restTemplate = new RestTemplate();

        if (reservationId != null) {
            try {
                ResponseEntity<ReservationSummaryDTO> response =
                        restTemplate.getForEntity(
                                reservationServiceUrl
                                        + "/api/v1/reservas/"
                                        + reservationId,
                                ReservationSummaryDTO.class
                        );
                ReservationSummaryDTO res = response.getBody();
                if (res == null) {
                    return new ResolvedContext(reservationId, null, null);
                }
                return new ResolvedContext(reservationId, res.getIdPaquete(), res.getIdViaje());
            } catch (RestClientException ex) {
                return new ResolvedContext(reservationId, null, null);
            }
        }

        try {
            ResponseEntity<ReservationSummaryDTO[]> response =
                    restTemplate.getForEntity(
                            reservationServiceUrl
                                    + "/api/v1/reservas/usuario/"
                                    + userId,
                            ReservationSummaryDTO[].class
                    );

            ReservationSummaryDTO[] reservations = response.getBody();

            if (reservations == null || reservations.length == 0) {
                throw new IllegalArgumentException(
                        "No existe reserva para el usuario " + userId
                );
            }

            return Arrays.stream(reservations)
                    .filter(r -> r.getId() != null)
                    .filter(r ->
                            r.getEstado() == null
                            || (
                                    !r.getEstado().equalsIgnoreCase("CANCELADA")
                                    && !r.getEstado().equalsIgnoreCase("PASADA")
                            )
                    )
                    .max(Comparator.comparing(ReservationSummaryDTO::getId))
                    .map(r -> new ResolvedContext(r.getId(), r.getIdPaquete(), r.getIdViaje()))
                    .orElseThrow(
                            () -> new IllegalArgumentException(
                                    "No existe reserva activa para el usuario " + userId
                            )
                    );
        } catch (RestClientException ex) {
            throw new IllegalStateException(
                    "No se pudo consultar la reserva del usuario",
                    ex
            );
        }
    }

    private record ResolvedContext(Integer reservationId, Long packageId, Long tripId) {}

    private DocumentType inferDocumentTypeFromFilename(String filename) {
        String normalizedFilename = normalizeDocumentName(filename);

        if (normalizedFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del archivo es obligatorio"
            );
        }

        List<DocumentType> matchingTypes = new ArrayList<>();

        for (DocumentType type : DocumentType.values()) {
            String normalizedType = normalizeDocumentName(type.name());

            if (normalizedFilename.equals(normalizedType)
                    || normalizedFilename.startsWith(normalizedType + "_")
                    || normalizedFilename.endsWith("_" + normalizedType)
                    || normalizedFilename.contains("_" + normalizedType + "_")) {
                matchingTypes.add(type);
            }
        }

        if (matchingTypes.isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del archivo debe incluir un tipo de documento valido: "
                            + Arrays.toString(DocumentType.values())
            );
        }

        if (matchingTypes.size() > 1) {
            throw new IllegalArgumentException(
                    "El nombre del archivo solo puede referenciar un tipo de documento"
            );
        }

        return matchingTypes.get(0);
    }

    private String normalizeDocumentName(String value) {
        String normalizedValue = Normalizer.normalize(
                value,
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");

        int extensionStart = normalizedValue.lastIndexOf('.');

        if (extensionStart > 0) {
            normalizedValue = normalizedValue.substring(0, extensionStart);
        }

        return normalizedValue
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}
