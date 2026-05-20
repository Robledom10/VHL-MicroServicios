package com.hernandolopera.gallery_service.dto.response;

import java.time.LocalDateTime;

import com.hernandolopera.gallery_service.model.MediaType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaResponse {
    private String id;
    private String url;
    private MediaType type;
    private Integer year;
    private LocalDateTime createdAt;
}
