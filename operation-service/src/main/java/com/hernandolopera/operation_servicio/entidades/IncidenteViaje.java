package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidente_viaje")
public class IncidenteViaje {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidente")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_viaje")
    public SalidaViaje viaje;
    public String tipo;
    public String descripcion;
    public LocalDateTime fechaIncidente;
    @Enumerated(EnumType.STRING)
    public EstadoIncidente estado = EstadoIncidente.pendiente;
}
