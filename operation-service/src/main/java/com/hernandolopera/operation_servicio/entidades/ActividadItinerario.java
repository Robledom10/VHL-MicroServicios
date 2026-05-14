package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "actividad_itinerario")
public class ActividadItinerario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete")
    public PaqueteTuristico paqueteTuristico;
    public Integer numeroDia;
    public String titulo;
    @Column(length = 500)
    public String descripcion;
    public LocalTime horaInicio;
    public LocalTime horaFin;
}
