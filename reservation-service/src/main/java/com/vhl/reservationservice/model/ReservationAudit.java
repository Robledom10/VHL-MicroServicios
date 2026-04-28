package com.vhl.reservationservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_audit", indexes = {
    @Index(name = "idx_audit_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_audit_created_at", columnList = "created_at")
})
public class ReservationAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(name = "old_status")
    @Enumerated(EnumType.STRING)
    private Reservation.ReservationStatus oldStatus;

    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private Reservation.ReservationStatus newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ReservationAudit() {
        this.createdAt = LocalDateTime.now();
    }

    public ReservationAudit(Long reservationId, AuditAction action) {
        this();
        this.reservationId = reservationId;
        this.action = action;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public Reservation.ReservationStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Reservation.ReservationStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Reservation.ReservationStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Reservation.ReservationStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum AuditAction {
        CREATED, CONFIRMED, CANCELLED, COMPLETED, UPDATED, NOTES_ADDED
    }
}
