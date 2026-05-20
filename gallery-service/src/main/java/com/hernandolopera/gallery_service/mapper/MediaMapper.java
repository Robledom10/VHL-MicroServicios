package com.hernandolopera.gallery_service.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.model.Media;

@Component
public class MediaMapper {
    public Media toEntity(CreatedMediaRequest request) {
        return Media.builder()
                .url(request.getUrl())
                .type(request.getType())
                .year(request.getYear())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public MediaResponse toResponse(Media media) {
        return MediaResponse.builder()
                .id(media.getId())
                .url(media.getUrl())
                .type(media.getType())
                .year(media.getYear())
                .createdAt(media.getCreatedAt())
                .build();
    }
}
