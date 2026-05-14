package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_cupo")
public class HistorialCupo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_cupo")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete")
    public PaqueteTuristico paqueteTuristico;
    public Integer cupoAnterior;
    public Integer cupoNuevo;
    public String motivo;
    public LocalDateTime fechaCambio = LocalDateTime.now();
}
