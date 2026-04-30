package com.vhl.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhl.reservationservice.model.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationResponseDTO {

    private Long id;
    @JsonProperty("idPaquete")
    private Long packageId;
    @JsonProperty("idUsuario")
    private Long userId;
    @JsonProperty("numeroCupos")
    private Integer numberOfSpots;
    @JsonProperty("precioTotal")
    private Double totalPrice;
    @JsonProperty("estado")
    private String status;
    @JsonProperty("codigoConfirmacion")
    private String confirmationCode;
    @JsonProperty("fechaCreacion")
    private LocalDateTime createdAt;
    @JsonProperty("fechaActualizacion")
    private LocalDateTime updatedAt;
    @JsonProperty("notas")
    private String notes;
    @JsonProperty("viajeros")
    private List<TravelerResponseDTO> travelers;
    @JsonProperty("montoDevolucion")
    private Double refundAmount;
    @JsonProperty("estadoDevolucion")
    private String refundStatus;
    @JsonProperty("fechaSolicitudDevolucion")
    private LocalDateTime refundRequestedAt;
    @JsonProperty("motivoCancelacion")
    private String cancellationReason;

    public ReservationResponseDTO() {
    }

    public ReservationResponseDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.packageId = reservation.getPackageId();
        this.userId = reservation.getUserId();
        this.numberOfSpots = reservation.getNumberOfSpots();
        this.totalPrice = reservation.getTotalPrice();
        this.status = reservation.getStatus().getDisplayName();
        this.confirmationCode = reservation.getConfirmationCode();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
        this.notes = reservation.getNotes();
        this.travelers = reservation.getTravelers().stream()
                .map(TravelerResponseDTO::new)
                .collect(Collectors.toList());
        this.refundAmount = reservation.getRefundAmount();
        this.refundStatus = reservation.getRefundStatus() != null
                ? reservation.getRefundStatus().getDisplayName()
                : null;
        this.refundRequestedAt = reservation.getRefundRequestedAt();
        this.cancellationReason = reservation.getCancellationReason();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setNumberOfSpots(Integer numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TravelerResponseDTO> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<TravelerResponseDTO> travelers) {
        this.travelers = travelers;
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
