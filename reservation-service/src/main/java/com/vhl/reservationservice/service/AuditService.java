package com.vhl.reservationservice.service;

import com.vhl.reservationservice.model.ReservationAudit;
import com.vhl.reservationservice.repository.ReservationAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private ReservationAuditRepository auditRepository;

    public void logAction(Long reservationId, ReservationAudit.AuditAction action) {
        logAction(reservationId, action, null, null, null);
    }

    public void logAction(Long reservationId, ReservationAudit.AuditAction action, String changedBy) {
        logAction(reservationId, action, null, null, changedBy);
    }

    public void logAction(Long reservationId, ReservationAudit.AuditAction action,
                          com.vhl.reservationservice.model.Reservation.ReservationStatus oldStatus,
                          com.vhl.reservationservice.model.Reservation.ReservationStatus newStatus,
                          String changedBy) {
        ReservationAudit audit = new ReservationAudit(reservationId, action);
        audit.setOldStatus(oldStatus);
        audit.setNewStatus(newStatus);
        audit.setChangedBy(changedBy != null ? changedBy : "SYSTEM");

        auditRepository.save(audit);
        logger.info("Acción auditada: Reserva {}, Acción: {}, Por: {}", 
                    reservationId, action, audit.getChangedBy());
    }

    public void logActionWithReason(Long reservationId, ReservationAudit.AuditAction action,
                                    String changedBy, String reason) {
        ReservationAudit audit = new ReservationAudit(reservationId, action);
        audit.setChangedBy(changedBy != null ? changedBy : "SYSTEM");
        audit.setChangeReason(reason);

        auditRepository.save(audit);
        logger.info("Acción auditada: Reserva {}, Acción: {}, Razón: {}", 
                    reservationId, action, reason);
    }
}
