package com.vhl.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TravelerRequestDTO {

    @JsonProperty("nombreCompleto")
    @JsonAlias("fullName")
    @NotBlank(message = "El nombre completo del viajero es obligatorio")
    private String fullName;

    @JsonProperty("tipoDocumento")
    @JsonAlias("documentType")
    @NotBlank(message = "El tipo de documento del viajero es obligatorio")
    private String documentType;

    @JsonProperty("numeroDocumento")
    @JsonAlias("documentNumber")
    @NotBlank(message = "El numero de documento del viajero es obligatorio")
    private String documentNumber;

    @JsonProperty("fechaNacimiento")
    @JsonAlias("birthDate")
    private LocalDate birthDate;

    @JsonProperty("telefono")
    @JsonAlias("phone")
    private String phone;

    @JsonProperty("correo")
    @JsonAlias("email")
    private String email;

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
