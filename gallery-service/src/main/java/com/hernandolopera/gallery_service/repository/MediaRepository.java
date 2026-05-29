package com.hernandolopera.gallery_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hernandolopera.gallery_service.model.Media;
import com.hernandolopera.gallery_service.model.MediaType;

public interface MediaRepository extends MongoRepository<Media, String> {

    List<Media> findByYear(Integer year);

    List<Media> findByType(MediaType type);

    List<Media> findByExcursion(String excursion);

    List<Media> findByActivity(String activity);

    List<Media> findByYearAndType(
            Integer year,
            MediaType type
    );

    List<Media> findByYearAndExcursion(
            Integer year,
            String excursion
    );
}