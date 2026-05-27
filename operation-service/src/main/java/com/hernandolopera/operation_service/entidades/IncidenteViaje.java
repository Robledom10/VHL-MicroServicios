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
@Table(name = "trip_incident")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenteViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "id_traveler")
    private Long idViajero;

    @Column(name = "type", nullable = false, length = 100)
    private String tipo;

    @Column(name = "description", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "severity", length = 30)
    private String severidad;

    @Column(name = "status", nullable = false, length = 30)
    private String estado;

    @Column(name = "reported_by", length = 120)
    private String reportadoPor;

    @Column(name = "incident_date", nullable = false)
    private LocalDateTime fechaRegistro;
}
