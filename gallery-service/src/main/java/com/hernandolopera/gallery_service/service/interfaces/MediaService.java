package com.hernandolopera.gallery_service.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.model.MediaType;

public interface MediaService {

    MediaResponse create(
            MultipartFile file,
            CreatedMediaRequest request
    );

    List<MediaResponse> getAll();

    MediaResponse getById(String id);

    List<MediaResponse> getByYear(Integer year);

    List<MediaResponse> getByType(MediaType type);

    List<MediaResponse> getByExcursion(String excursion);

    List<MediaResponse> getByActivity(String activity);

    List<MediaResponse> getByYearAndType(
            Integer year,
            MediaType type
    );

    List<MediaResponse> getByYearAndExcursion(
            Integer year,
            String excursion
    );

    void delete(String id);
}