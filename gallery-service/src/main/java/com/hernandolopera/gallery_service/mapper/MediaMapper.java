package com.hernandolopera.gallery_service.mapper;

import org.springframework.stereotype.Component;

import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.model.Media;

@Component
public class MediaMapper {

    public MediaResponse toResponse(Media media) {

        return MediaResponse.builder()
                .id(media.getId())
                .url(media.getUrl())
                .publicId(media.getPublicId())
                .type(media.getType())
                .year(media.getYear())
                .excursion(media.getExcursion())
                .location(media.getLocation())
                .folder(media.getFolder())
                .createdAt(media.getCreatedAt())
                .build();
    }
}