package com.documents.repository;

import com.documents.entity.DocumentValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentValidationRepository
        extends JpaRepository<DocumentValidation,Integer> {

    List<DocumentValidation>
    findByTravelerDocumentIdDocument(
            Integer documentId
    );

    void deleteByTravelerDocumentIdDocument(
            Integer documentId
    );
}
