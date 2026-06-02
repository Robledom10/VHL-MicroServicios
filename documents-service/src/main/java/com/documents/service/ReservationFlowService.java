package com.documents.service;

public interface ReservationFlowService {

    boolean canContinueReservation(
            Integer userId,
            Integer reservationId
    );
}
