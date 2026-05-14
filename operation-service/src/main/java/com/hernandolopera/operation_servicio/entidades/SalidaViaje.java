package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salida_viaje")
public class SalidaViaje {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    public Integer id;
    public Integer idUsuario;
    public Integer idPaquete;
    public LocalDate fechaSalida;
    public LocalDate fechaRetorno;
    @Enumerated(EnumType.STRING)
    public EstadoViaje estado = EstadoViaje.programado;
}
