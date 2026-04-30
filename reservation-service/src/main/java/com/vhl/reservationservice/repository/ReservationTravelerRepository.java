package com.vhl.reservationservice.repository;

import com.vhl.reservationservice.model.ReservationTraveler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationTravelerRepository extends JpaRepository<ReservationTraveler, Long> {
    List<ReservationTraveler> findByReservationId(Long reservationId);
    void deleteByReservationId(Long reservationId);
}
