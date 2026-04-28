package com.vhl.reservationservice.service;

import com.vhl.reservationservice.dto.ReservationRequestDTO;
import com.vhl.reservationservice.dto.ReservationResponseDTO;
import com.vhl.reservationservice.model.Reservation;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO);
    ReservationResponseDTO getReservation(Long id);
    List<ReservationResponseDTO> getReservationsByUserId(Long userId);
    List<ReservationResponseDTO> getReservationsByPackageId(Long packageId);
    ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO requestDTO);
    void cancelReservation(Long id);
    void confirmReservation(Long id);
    List<ReservationResponseDTO> getAllReservations();
}
