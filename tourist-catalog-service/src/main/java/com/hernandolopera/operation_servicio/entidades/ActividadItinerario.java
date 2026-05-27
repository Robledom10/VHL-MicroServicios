package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "itinerarie")
public class ActividadItinerario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_itinerary")
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_package", nullable = false)
    public PaqueteTuristico paqueteTuristico;

    @Column(name = "day_number", nullable = false)
    public Integer numeroDia;

    @Column(name = "title", nullable = false, length = 150)
    public String titulo;
}
