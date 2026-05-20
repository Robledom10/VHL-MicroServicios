package com.hernandolopera.gallery_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.exception.ResourceNotFoundException;
import com.hernandolopera.gallery_service.mapper.MediaMapper;
import com.hernandolopera.gallery_service.model.Media;
import com.hernandolopera.gallery_service.model.MediaType;
import com.hernandolopera.gallery_service.repository.MediaRepository;
import com.hernandolopera.gallery_service.service.interfaces.MediaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;

    @Override
    public MediaResponse create(CreatedMediaRequest request) {
        Media media = mediaMapper.toEntity(request);

        Media savedMedia = mediaRepository.save(media);

        return mediaMapper.toResponse(savedMedia);
    }

    @Override
    public List<MediaResponse> getAll() {
        return mediaRepository.findAll()
                .stream()
                .map(mediaMapper::toResponse)
                .toList();
    }

    @Override
    public MediaResponse getById(String id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        return mediaMapper.toResponse(media);
    }

    @Override
    public List<MediaResponse> getByYear(Integer year) {
        return mediaRepository.findByYear(year)
                .stream()
                .map(mediaMapper::toResponse)
                .toList();
    }

    @Override
    public List<MediaResponse> getByType(MediaType type) {

        return mediaRepository.findByType(type)
                .stream()
                .map(mediaMapper::toResponse)
                .toList();
    }

    @Override
    public List<MediaResponse> getByYearAndType(Integer year, MediaType type) {
        return mediaRepository.findByYearAndType(year, type)
                .stream()
                .map(mediaMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        mediaRepository.delete(media);
    }

}
