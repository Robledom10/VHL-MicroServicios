package com.vhl.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhl.reservationservice.model.Reservation;

import java.time.LocalDateTime;

public class CancelReservationResponseDTO {

    @JsonProperty("idReserva")
    private Long reservationId;
    @JsonProperty("estadoReserva")
    private String reservationStatus;
    @JsonProperty("montoDevolucion")
    private Double refundAmount;
    @JsonProperty("estadoDevolucion")
    private String refundStatus;
    @JsonProperty("fechaSolicitudDevolucion")
    private LocalDateTime refundRequestedAt;
    @JsonProperty("motivoCancelacion")
    private String cancellationReason;

    public CancelReservationResponseDTO() {
    }

    public CancelReservationResponseDTO(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.reservationStatus = reservation.getStatus().getDisplayName();
        this.refundAmount = reservation.getRefundAmount();
        this.refundStatus = reservation.getRefundStatus() != null
                ? reservation.getRefundStatus().getDisplayName()
                : null;
        this.refundRequestedAt = reservation.getRefundRequestedAt();
        this.cancellationReason = reservation.getCancellationReason();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public LocalDateTime getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public void setRefundRequestedAt(LocalDateTime refundRequestedAt) {
        this.refundRequestedAt = refundRequestedAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
