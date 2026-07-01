package com.documents.service.impl;

import com.documents.dto.TravelerFileDTO;
import com.documents.entity.TravelerDocument;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.TravelerFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelerFileServiceImpl
        implements TravelerFileService {

    private final TravelerDocumentRepository repository;

    @Override
    public TravelerFileDTO getTravelerFile(
            Integer userId
    ) {

        List<TravelerDocument> documents =
                repository.findByUserId(userId);

        TravelerDocument first = documents.isEmpty() ? null : documents.get(0);

        return TravelerFileDTO.builder()
                .userId(userId)
                .reservationId(first == null ? null : first.getReservationId())
                .packageId(first == null ? null : first.getPackageId())
                .tripId(first == null ? null : first.getTripId())
                .documents(documents)
                .build();
    }
}