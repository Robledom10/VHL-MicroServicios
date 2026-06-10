package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "traveler_medical_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformacionMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medical_info")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "id_traveler", nullable = false)
    private Long idViajero;

    @Column(name = "blood_type", nullable = false, length = 10)
    private String tipoSangre;

    @Column(name = "allergies", length = 500)
    private String alergias;

    @Column(name = "medications", length = 500)
    private String medicamentos;

    @Column(name = "medical_conditions", length = 500)
    private String condicionesMedicas;

    @Column(name = "medical_phone", length = 30)
    private String telefonoMedico;

    @Column(name = "traveler_name", length = 150)
    private String nombreViajero;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
