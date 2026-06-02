package com.documents.repository;

import com.documents.entity.ContractAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractAcceptanceRepository
        extends JpaRepository<ContractAcceptance,Integer> {

    boolean existsByIdReservationAndIdUser(
            Integer idReservation,
            Integer idUser
    );
}
