package com.vhl.reservationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequestDTO {

    @NotNull(message = "Package ID is required")
    private Long packageId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Number of spots is required")
    @Positive(message = "Number of spots must be greater than 0")
    private Integer numberOfSpots;

    private String notes;

    public ReservationRequestDTO() {
    }

    public ReservationRequestDTO(Long packageId, Long userId, Integer numberOfSpots) {
        this.packageId = packageId;
        this.userId = userId;
        this.numberOfSpots = numberOfSpots;
    }

    // Getters and Setters
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
