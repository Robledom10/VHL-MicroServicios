package com.documents.service;

import com.documents.dto.IAResponseDTO;
import com.documents.entity.TravelerDocument;

public interface IAService {

    IAResponseDTO analyzeDocument(
            TravelerDocument document
    );

}
