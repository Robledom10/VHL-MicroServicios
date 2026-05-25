package com.hernandolopera.gallery_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.exception.ResourceNotFoundException;
import com.hernandolopera.gallery_service.mapper.MediaMapper;
import com.hernandolopera.gallery_service.model.Media;
import com.hernandolopera.gallery_service.model.MediaType;
import com.hernandolopera.gallery_service.repository.MediaRepository;
import com.hernandolopera.gallery_service.service.interfaces.MediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 👈 Agregado para ver logs reales

@Service
@RequiredArgsConstructor
@Slf4j // 👈 Anotación de Lombok para habilitar el objeto 'log'
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final Cloudinary cloudinary;

    @Override
    public MediaResponse create(MultipartFile file, CreatedMediaRequest request) {
        try {
            String folder = String.format(
                    "gallery-service/excursions/%d/%s",
                    request.getYear(),
                    request.getExcursion().toLowerCase().replace(" ", "-"));

            log.info("Iniciando subida de archivo a Cloudinary en la carpeta: {}", folder);

            // Configuramos un timeout local para que Cloudinary no congele el hilo de Java
            var options = ObjectUtils.asMap(
                    "folder", folder,
                    "timeout", 3000 // 👈 Fuerza a Cloudinary a abortar tras 3 segundos si no responde
            );

            var uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            log.info("Subida exitosa a Cloudinary. Public ID: {}", uploadResult.get("public_id"));

            Media media = Media.builder()
                    .url(uploadResult.get("secure_url").toString())
                    .publicId(uploadResult.get("public_id").toString())
                    .type(request.getType())
                    .year(request.getYear())
                    .excursion(request.getExcursion())
                    .location(request.getLocation())
                    .folder(folder)
                    .createdAt(LocalDateTime.now())
                    .build();

            Media savedMedia = mediaRepository.save(media);
            return mediaMapper.toResponse(savedMedia);

        } catch (Exception e) {
            // 🟥 CRÍTICO: Esto pintará en tu consola de Docker el verdadero error (Falta de
            // credenciales, Red, etc.)
            log.error("Error fatal al subir los medios a Cloudinary. Causa real: ", e);
            throw new RuntimeException("Error uploading media: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MediaResponse> getAll() {
        log.info("Ejecutando findAll() en la base de datos de la galería.");
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
        return mediaRepository.findByYear(year).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public List<MediaResponse> getByType(MediaType type) {
        return mediaRepository.findByType(type).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public List<MediaResponse> getByExcursion(String excursion) {
        return mediaRepository.findByExcursion(excursion).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public List<MediaResponse> getByLocation(String location) {
        return mediaRepository.findByLocation(location).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public List<MediaResponse> getByYearAndType(Integer year, MediaType type) {
        return mediaRepository.findByYearAndType(year, type).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public List<MediaResponse> getByYearAndExcursion(Integer year, String excursion) {
        return mediaRepository.findByYearAndExcursion(year, excursion).stream().map(mediaMapper::toResponse).toList();
    }

    @Override
    public void delete(String id) {
        try {
            Media media = mediaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

            log.info("Eliminando archivo de Cloudinary con public_id: {}", media.getPublicId());
            cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());

            mediaRepository.delete(media);
            log.info("Registro eliminado de MongoDB con éxito.");

        } catch (Exception e) {
            log.error("Error al eliminar los medios. Causa real: ", e);
            throw new RuntimeException("Error deleting media: " + e.getMessage(), e);
        }
    }
}