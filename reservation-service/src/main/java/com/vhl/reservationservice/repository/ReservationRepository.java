package com.vhl.reservationservice.repository;

import com.vhl.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByPackageId(Long packageId);
    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);
}
