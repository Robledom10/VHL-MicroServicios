package com.hernandolopera.operation_servicio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidente_viaje")
public class IncidenteViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidente")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_viaje", nullable = false)
    private SalidaViaje trip;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime incidentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoIncidente estado = EstadoIncidente.pendiente;
}
