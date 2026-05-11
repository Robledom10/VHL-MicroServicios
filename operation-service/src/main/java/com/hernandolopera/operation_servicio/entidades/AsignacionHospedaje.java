package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "asignacion_hospedaje")
public class AsignacionHospedaje {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hospedaje")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_viaje")
    public SalidaViaje viaje;
    public String nombreHotel;
    public String direccion;
    public LocalDate fechaEntrada;
    public LocalDate fechaSalidaHospedaje;
}
