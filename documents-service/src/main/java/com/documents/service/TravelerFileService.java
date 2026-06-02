package com.documents.service;

import com.documents.dto.TravelerFileDTO;

public interface TravelerFileService {

    TravelerFileDTO getTravelerFile(
            Integer userId
    );
}
