package com.documents.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;

@Repository
public interface TravelerDocumentRepository
        extends JpaRepository<TravelerDocument, Integer> {

    List<TravelerDocument> findByUserId(Integer userId);
    List<TravelerDocument> findByReservationId(Integer reservationId);
    List<TravelerDocument> findByStatus(DocumentStatus status);
}