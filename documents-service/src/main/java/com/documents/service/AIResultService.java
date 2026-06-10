package com.documents.service;

import com.documents.dto.AIValidationResultDTO;

public interface AIResultService {

    void processResult(
            AIValidationResultDTO dto
    );
}