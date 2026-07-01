package com.documents.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.documents.entity.TravelerDocument;
import com.documents.entity.enums.DocumentStatus;
import com.documents.repository.ContractAcceptanceRepository;
import com.documents.repository.TravelerDocumentRepository;
import com.documents.service.ReservationFlowService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationFlowServiceImpl
        implements ReservationFlowService {

    private final TravelerDocumentRepository repository;

    private final ContractAcceptanceRepository contractRepository;

    @Override
    public boolean canContinueReservation(
            Integer userId,
            Integer reservationId
    ) {

        // Validar documentos
        List<TravelerDocument> documents =
                repository.findByReservationId(reservationId)
                        .stream()
                        .filter(document ->
                                userId.equals(document.getUserId())
                        )
                        .toList();

        boolean documentsApproved =
                !documents.isEmpty()
                && documents.stream()
                        .allMatch(doc ->
                                doc.getStatus()
                                        == DocumentStatus.aprobado
                        );

        if (!documentsApproved) {
            return false;
        }

        // Validar contrato aceptado
        boolean contractAccepted =
                contractRepository
                        .existsByIdReservationAndIdUser(
                                reservationId,
                                userId
                        );

        return contractAccepted;
    }
}
