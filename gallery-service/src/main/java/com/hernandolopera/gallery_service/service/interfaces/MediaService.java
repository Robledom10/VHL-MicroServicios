package com.hernandolopera.gallery_service.service.interfaces;

import java.util.List;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.model.MediaType;

public interface MediaService {
    MediaResponse create(CreatedMediaRequest request);

    List<MediaResponse> getAll();

    MediaResponse getById(String id);

    List<MediaResponse> getByYear(Integer year);

    List<MediaResponse> getByType(MediaType type);

    List<MediaResponse> getByYearAndType(Integer year, MediaType type);

    void delete(String id);

}
