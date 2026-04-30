package com.vhl.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ReservationRequestDTO {

    @JsonProperty("idPaquete")
    @JsonAlias("packageId")
    @NotNull(message = "El ID del paquete es obligatorio")
    private Long packageId;

    @JsonProperty("idUsuario")
    @JsonAlias("userId")
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @JsonProperty("numeroCupos")
    @JsonAlias("numberOfSpots")
    @NotNull(message = "El numero de cupos es obligatorio")
    @Positive(message = "El numero de cupos debe ser mayor a 0")
    private Integer numberOfSpots;

    @JsonProperty("notas")
    @JsonAlias("notes")
    private String notes;

    @JsonProperty("viajeros")
    @JsonAlias("travelers")
    @Valid
    private List<TravelerRequestDTO> travelers;

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

    public List<TravelerRequestDTO> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<TravelerRequestDTO> travelers) {
        this.travelers = travelers;
    }
}
