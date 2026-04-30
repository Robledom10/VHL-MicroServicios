package com.vhl.reservationservice.service;

import com.vhl.reservationservice.dto.ReservationRequestDTO;
import com.vhl.reservationservice.dto.ReservationResponseDTO;
import com.vhl.reservationservice.dto.CancelReservationRequestDTO;
import com.vhl.reservationservice.dto.CancelReservationResponseDTO;
import com.vhl.reservationservice.dto.TravelerRequestDTO;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO);
    ReservationResponseDTO getReservation(Long id);
    List<ReservationResponseDTO> getReservationsByUserId(Long userId);
    List<ReservationResponseDTO> getReservationsByPackageId(Long packageId);
    ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO requestDTO);
    CancelReservationResponseDTO cancelReservation(Long id, CancelReservationRequestDTO requestDTO);
    void confirmReservation(Long id);
    List<ReservationResponseDTO> getAllReservations();
    ReservationResponseDTO registerTravelers(Long id, List<TravelerRequestDTO> travelers);
}
