package com.vhl.reservationservice.repository;

import com.vhl.reservationservice.model.ReservationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationAuditRepository extends JpaRepository<ReservationAudit, Long> {
    List<ReservationAudit> findByReservationId(Long reservationId);
    List<ReservationAudit> findByReservationIdOrderByCreatedAtDesc(Long reservationId);
    List<ReservationAudit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
