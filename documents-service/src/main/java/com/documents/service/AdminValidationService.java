package com.documents.service;

import com.documents.dto.AdminDocumentValidationDTO;
import com.documents.entity.TravelerDocument;

public interface AdminValidationService {

    TravelerDocument approveDocument(
            Integer documentId,
            AdminDocumentValidationDTO dto
    );

    TravelerDocument rejectDocument(
            Integer documentId,
            AdminDocumentValidationDTO dto
    );

    TravelerDocument markInProcess(
            Integer documentId,
            AdminDocumentValidationDTO dto
    );
}
