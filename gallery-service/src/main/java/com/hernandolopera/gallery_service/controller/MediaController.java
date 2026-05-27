package com.hernandolopera.gallery_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hernandolopera.gallery_service.dto.request.CreatedMediaRequest;
import com.hernandolopera.gallery_service.dto.response.MediaResponse;
import com.hernandolopera.gallery_service.model.MediaType;
import com.hernandolopera.gallery_service.service.interfaces.MediaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Validated
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<MediaResponse> create(
            @Valid @RequestBody CreatedMediaRequest request) {

        MediaResponse response = mediaService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MediaResponse>> getAll() {

        return ResponseEntity.ok(mediaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(
            @PathVariable String id) {

        return ResponseEntity.ok(mediaService.getById(id));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<MediaResponse>> getByYear(
            @PathVariable Integer year) {

        return ResponseEntity.ok(mediaService.getByYear(year));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MediaResponse>> getByType(
            @PathVariable MediaType type) {

        return ResponseEntity.ok(mediaService.getByType(type));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MediaResponse>> getByYearAndType(
            @RequestParam Integer year,
            @RequestParam MediaType type) {

        return ResponseEntity.ok(
                mediaService.getByYearAndType(year, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {

        mediaService.delete(id);

        return ResponseEntity.noContent().build();
    }
}