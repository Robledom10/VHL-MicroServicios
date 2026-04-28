package com.vhl.reservationservice.service;

import com.vhl.reservationservice.model.Reservation;
import com.vhl.reservationservice.model.ReservationAudit;
import org.springframework.context.ApplicationEvent;

public class ReservationEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final ReservationAudit.AuditAction action;
    private final String reason;

    public ReservationEvent(Object source, Reservation reservation, ReservationAudit.AuditAction action) {
        super(source);
        this.reservation = reservation;
        this.action = action;
        this.reason = "";
    }

    public ReservationEvent(Object source, Reservation reservation, ReservationAudit.AuditAction action, String reason) {
        super(source);
        this.reservation = reservation;
        this.action = action;
        this.reason = reason;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ReservationAudit.AuditAction getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }
}
