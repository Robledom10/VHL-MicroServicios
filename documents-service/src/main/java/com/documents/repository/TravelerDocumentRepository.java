package com.documents.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.documents.entity.TravelerDocument;

@Repository
public interface TravelerDocumentRepository
        extends JpaRepository<TravelerDocument, Integer> {

    List<TravelerDocument> findByUserId(Integer userId);

}