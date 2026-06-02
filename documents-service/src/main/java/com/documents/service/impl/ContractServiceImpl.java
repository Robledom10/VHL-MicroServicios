package com.documents.service.impl;

import com.documents.dto.ContractAcceptanceDTO;
import com.documents.entity.ContractAcceptance;
import com.documents.repository.ContractAcceptanceRepository;
import com.documents.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl
        implements ContractService {

    private final ContractAcceptanceRepository repository;

    @Override
    public String getContract() {

        return """
                CONTRATO DE PRESTACIÓN DE SERVICIOS TURÍSTICOS

                """;
    }

    @Override
    public void acceptContract(
            ContractAcceptanceDTO dto
    ) {

        if(dto.getElectronicSignature() == null
                || dto.getElectronicSignature().isBlank()) {

            throw new RuntimeException(
                    "Firma electrónica requerida"
            );
        }

        ContractAcceptance acceptance =
                ContractAcceptance.builder()
                        .idReservation(
                                dto.getReservationId()
                        )
                        .idUser(
                                dto.getUserId()
                        )
                        .contractVersion(
                                dto.getContractVersion()
                        )
                        .electronicSignature(
                                dto.getElectronicSignature()
                        )
                        .acceptedAt(
                                LocalDateTime.now()
                        )
                        .build();

        repository.save(acceptance);
    }

    @Override
    public boolean hasAcceptedContract(
            Integer reservationId,
            Integer userId
    ) {

        return repository
                .existsByIdReservationAndIdUser(
                        reservationId,
                        userId
                );
    }
}