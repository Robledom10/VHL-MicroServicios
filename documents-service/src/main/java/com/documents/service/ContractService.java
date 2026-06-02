package com.documents.service;

import com.documents.dto.ContractAcceptanceDTO;

public interface ContractService {

    String getContract();

    void acceptContract(
            ContractAcceptanceDTO dto
    );

    boolean hasAcceptedContract(
            Integer reservationId,
            Integer userId
    );
}
