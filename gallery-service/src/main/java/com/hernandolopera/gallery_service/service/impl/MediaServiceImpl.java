package com.hernandolopera.gallery_service.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

        private final MediaRepository mediaRepository;
        private final MediaMapper mediaMapper;
        private final Cloudinary cloudinary;

        @Override
        public List<MediaResponse> create(
                        List<MultipartFile> files,
                        CreatedMediaRequest request) {

                List<MediaResponse> responses = new ArrayList<>();

                try {
                        String folder = String.format(
                                        "gallery-service/excursions/%d/%s",
                                        request.getYear(),
                                        request.getExcursion()
                                                        .toLowerCase()
                                                        .replace(" ", "-"));

                        log.info("Iniciando subida múltiple de archivos a Cloudinary");

                        loopArchivos: for (MultipartFile file : files) {

                                if (file == null || file.isEmpty()) {
                                        continue loopArchivos;
                                }

                                log.info("====================================");
                                log.info("Archivo: {}", file.getOriginalFilename());
                                log.info("Tamaño: {} MB", file.getSize() / 1024 / 1024);

                                Map<?, ?> uploadResult;

                                if (request.getType() == MediaType.VIDEO) {
                                        log.info("Subiendo VIDEO con uploadLarge()");
                                        uploadResult = cloudinary.uploader().uploadLarge(
                                                        file.getInputStream(),
                                                        ObjectUtils.asMap(
                                                                        "folder", folder,
                                                                        "resource_type", "video",
                                                                        "chunk_size", 20000000));
                                } else {
                                        log.info("Subiendo IMAGEN con upload()");
                                        uploadResult = cloudinary.uploader().upload(
                                                        file.getInputStream(),
                                                        ObjectUtils.asMap("folder", folder, "resource_type", "image"));
                                }

                                log.info("UPLOAD RESULT: {}", uploadResult);

                                if (uploadResult == null
                                                || uploadResult.get("secure_url") == null
                                                || uploadResult.get("public_id") == null) {
                                        throw new RuntimeException("Cloudinary no retornó secure_url o public_id");
                                }

                                Media media = Media.builder()
                                                .url(uploadResult.get("secure_url").toString())
                                                .publicId(uploadResult.get("public_id").toString())
                                                .type(request.getType())
                                                .year(request.getYear())
                                                .excursion(request.getExcursion())
                                                .activity(request.getActivity())
                                                .folder(folder)
                                                .createdAt(LocalDateTime.now())
                                                .build();

                                Media savedMedia = mediaRepository.save(media);
                                responses.add(mediaMapper.toResponse(savedMedia));

                                log.info("Archivo guardado correctamente: {}", savedMedia.getPublicId());
                        }

                        return responses;

                } catch (Exception e) {
                        log.error("Error fatal al subir medios a Cloudinary", e);
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
        public List<MediaResponse> getByExcursion(String excursion) {
                return mediaRepository.findByExcursion(excursion)
                                .stream()
                                .map(mediaMapper::toResponse)
                                .toList();
        }

        @Override
        public List<MediaResponse> getByActivity(String activity) {
                return mediaRepository.findByActivity(activity)
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
        public List<MediaResponse> getByYearAndExcursion(Integer year, String excursion) {
                return mediaRepository.findByYearAndExcursion(year, excursion)
                                .stream()
                                .map(mediaMapper::toResponse)
                                .toList();
        }

        @Override
        public MediaResponse update(
                        String id,
                        MultipartFile file,
                        CreatedMediaRequest request) {

                try {
                        Media media = mediaRepository.findById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Media not found with id: " + id));

                        String folder = String.format(
                                        "gallery-service/excursions/%d/%s",
                                        request.getYear(),
                                        request.getExcursion()
                                                        .toLowerCase()
                                                        .replace(" ", "-"));

                        /*
                         * SI VIENE NUEVO ARCHIVO (REEMPLAZO)
                         */
                        if (file != null && !file.isEmpty()) {

                                log.info("Eliminando archivo anterior de Cloudinary: {}", media.getPublicId());

                                try {
                                        cloudinary.uploader().destroy(
                                                        media.getPublicId(),
                                                        ObjectUtils.asMap(
                                                                        "resource_type",
                                                                        media.getType() == MediaType.VIDEO ? "video"
                                                                                        : "image"));
                                } catch (Exception e) {
                                        log.warn("No se pudo destruir el archivo previo en Cloudinary: {}",
                                                        e.getMessage());
                                }

                                var options = ObjectUtils.asMap(
                                                "folder", folder,
                                                "resource_type", "auto",
                                                "chunk_size", 6000000,
                                                "timeout", 600000);

                                Map<?, ?> uploadResult;

                                if (request.getType() == MediaType.VIDEO) {
                                        log.info("Subiendo video con uploadLarge()");
                                        uploadResult = cloudinary.uploader().uploadLarge(file.getBytes(), options);
                                } else {
                                        log.info("Subiendo imagen con upload()");
                                        uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                                }

                                log.info("UPLOAD RESULT: {}", uploadResult);

                                media.setUrl(uploadResult.get("secure_url").toString());
                                media.setPublicId(uploadResult.get("public_id").toString());

                        } else {
                                /*
                                 * SOLO CAMBIA METADATA / MOVER ARCHIVO ENTRE CARPETAS EN LA NUBE
                                 */
                                String oldPublicId = media.getPublicId();
                                String fileName = oldPublicId.substring(oldPublicId.lastIndexOf("/") + 1);
                                String newPublicId = folder + "/" + fileName;

                                log.info("OLD PUBLIC ID: {}", oldPublicId);
                                log.info("NEW PUBLIC ID: {}", newPublicId);

                                if (!oldPublicId.equals(newPublicId)) {
                                        try {
                                                // 🛠️ Se define dinámicamente si es video o imagen para evitar el
                                                // "Resource not found"
                                                String resourceType = (media.getType() == MediaType.VIDEO) ? "video"
                                                                : "image";

                                                Map<?, ?> renameResult = cloudinary.uploader().rename(
                                                                oldPublicId,
                                                                newPublicId,
                                                                ObjectUtils.asMap(
                                                                                "overwrite", true,
                                                                                "resource_type", resourceType));

                                                log.info("RENAME RESULT: {}", renameResult);

                                                media.setPublicId(renameResult.get("public_id").toString());
                                                media.setUrl(renameResult.get("secure_url").toString());

                                        } catch (Exception cloudinaryException) {
                                                // 🛡️ Si fue borrado desde la consola web de Cloudinary, no tumbamos el
                                                // flujo de la BD
                                                log.error("Advertencia de sincronización en Cloudinary (El recurso no se movió): {}",
                                                                cloudinaryException.getMessage());

                                                media.setPublicId(newPublicId);
                                        }
                                }
                        }

                        media.setType(request.getType());
                        media.setYear(request.getYear());
                        media.setExcursion(request.getExcursion());
                        media.setActivity(request.getActivity());
                        media.setFolder(folder);

                        Media updatedMedia = mediaRepository.save(media);
                        log.info("Media actualizada correctamente");

                        return mediaMapper.toResponse(updatedMedia);

                } catch (Exception e) {
                        log.error("Error actualizando media: ", e);
                        throw new RuntimeException("Error updating media: " + e.getMessage(), e);
                }
        }

        @Override
        public void delete(String id) {
                try {
                        Media media = mediaRepository.findById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Media not found with id: " + id));

                        log.info("Eliminando archivo de Cloudinary con public_id: {}", media.getPublicId());

                        try {
                                String resourceType = (media.getType() == MediaType.VIDEO) ? "video" : "image";
                                cloudinary.uploader().destroy(
                                                media.getPublicId(),
                                                ObjectUtils.asMap("resource_type", resourceType));
                        } catch (Exception e) {
                                log.warn("El archivo no se pudo eliminar de Cloudinary (posiblemente ya no existía): {}",
                                                e.getMessage());
                        }

                        mediaRepository.delete(media);
                        log.info("Registro eliminado de MongoDB con éxito.");

                } catch (Exception e) {
                        log.error("Error al eliminar los medios. Causa real: ", e);
                        throw new RuntimeException("Error deleting media: " + e.getMessage(), e);
                }
        }
}