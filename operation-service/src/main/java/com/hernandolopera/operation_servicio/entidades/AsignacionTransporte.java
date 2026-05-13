package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion_transporte")
public class AsignacionTransporte {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transporte")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_viaje")
    public SalidaViaje viaje;
    public String empresa;
    public String tipoVehiculo;
    public String placa;
    public LocalDateTime horaSalida;
}
