package com.vhl.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhl.reservationservice.model.ReservationTraveler;

import java.time.LocalDate;

public class TravelerResponseDTO {

    private Long id;
    @JsonProperty("nombreCompleto")
    private String fullName;
    @JsonProperty("tipoDocumento")
    private String documentType;
    @JsonProperty("numeroDocumento")
    private String documentNumber;
    @JsonProperty("fechaNacimiento")
    private LocalDate birthDate;
    @JsonProperty("telefono")
    private String phone;
    @JsonProperty("correo")
    private String email;

    public TravelerResponseDTO() {
    }

    public TravelerResponseDTO(ReservationTraveler traveler) {
        this.id = traveler.getId();
        this.fullName = traveler.getFullName();
        this.documentType = traveler.getDocumentType();
        this.documentNumber = traveler.getDocumentNumber();
        this.birthDate = traveler.getBirthDate();
        this.phone = traveler.getPhone();
        this.email = traveler.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
