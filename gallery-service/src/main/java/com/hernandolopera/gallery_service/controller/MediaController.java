package com.hernandolopera.gallery_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.service.interfaces.MediaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Validated
public class MediaController {

    private final MediaService mediaService;

    private final ObjectMapper objectMapper;

    /**
     * Uploads a media file to Cloudinary and stores its metadata in MongoDB.
     *
     * @param file uploaded media file
     * @param data media metadata in JSON format
     * @return created media response
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> create(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String data) throws Exception {

        CreatedMediaRequest request =
                objectMapper.readValue(
                        data,
                        CreatedMediaRequest.class
                );

        MediaResponse response =
                mediaService.create(file, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all media records.
     *
     * @return list of media
     */
    @GetMapping
    public ResponseEntity<List<MediaResponse>> getAll() {

        return ResponseEntity.ok(
                mediaService.getAll()
        );
    }

    /**
     * Retrieves a media record by its identifier.
     *
     * @param id media identifier
     * @return media response
     */
    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                mediaService.getById(id)
        );
    }

    /**
     * Retrieves media records filtered by year.
     *
     * @param year excursion year
     * @return filtered media list
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<MediaResponse>> getByYear(
            @PathVariable Integer year) {

        return ResponseEntity.ok(
                mediaService.getByYear(year)
        );
    }

    /**
     * Retrieves media records filtered by media type.
     *
     * @param type media type
     * @return filtered media list
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MediaResponse>> getByType(
            @PathVariable com.hernandolopera.gallery_service.model.MediaType type) {

        return ResponseEntity.ok(
                mediaService.getByType(type)
        );
    }

    /**
     * Retrieves media records filtered by excursion.
     *
     * @param excursion excursion name
     * @return filtered media list
     */
    @GetMapping("/excursion/{excursion}")
    public ResponseEntity<List<MediaResponse>> getByExcursion(
            @PathVariable String excursion) {

        return ResponseEntity.ok(
                mediaService.getByExcursion(excursion)
        );
    }

    /**
     * Retrieves media records filtered by location.
     *
     * @param location excursion location
     * @return filtered media list
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<MediaResponse>> getByLocation(
            @PathVariable String location) {

        return ResponseEntity.ok(
                mediaService.getByLocation(location)
        );
    }

    /**
     * Retrieves media records filtered by year and media type.
     *
     * @param year excursion year
     * @param type media type
     * @return filtered media list
     */
    @GetMapping("/filter")
    public ResponseEntity<List<MediaResponse>> getByYearAndType(
            @RequestParam Integer year,
            @RequestParam com.hernandolopera.gallery_service.model.MediaType type) {

        return ResponseEntity.ok(
                mediaService.getByYearAndType(year, type)
        );
    }

    /**
     * Retrieves media records filtered by year and excursion.
     *
     * @param year excursion year
     * @param excursion excursion name
     * @return filtered media list
     */
    @GetMapping("/filter/excursion")
    public ResponseEntity<List<MediaResponse>> getByYearAndExcursion(
            @RequestParam Integer year,
            @RequestParam String excursion) {

        return ResponseEntity.ok(
                mediaService.getByYearAndExcursion(
                        year,
                        excursion
                )
        );
    }

    /**
     * Deletes a media record and its Cloudinary file.
     *
     * @param id media identifier
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {

        mediaService.delete(id);

        return ResponseEntity.noContent().build();
    }
}