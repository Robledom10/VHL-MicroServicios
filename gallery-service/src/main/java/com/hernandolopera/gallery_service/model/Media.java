package com.hernandolopera.gallery_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media")
public class Media {

    @Id
    private String id;

    /**
     * Cloudinary secure URL
     */
    private String url;

    /**
     * Cloudinary public identifier
     */
    private String publicId;

    /**
     * Media type (IMAGE or VIDEO)
     */
    private MediaType type;

    /**
     * Excursion year
     */
    private Integer year;

    /**
     * Excursion name
     * Example:
     * - Cartagena
     * - Guatape
     * - San Andres
     */
    private String excursion;

    /**
     * Excursion location
     * Example:
     * - Bolivar
     * - Antioquia
     * - San Andres Islands
     */
    private String location;

    /**
     * Cloudinary folder structure
     * Example:
     * gallery-service/excursions/2025/cartagena
     */
    private String folder;

    /**
     * Media creation date
     */
    private LocalDateTime createdAt;
}