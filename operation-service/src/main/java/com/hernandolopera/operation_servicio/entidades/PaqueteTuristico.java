package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paquete_turistico")
public class PaqueteTuristico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete")
    public Integer id;
    public String nombre;
    public String categoria;
    public String destino;
    @Column(length = 1000)
    public String descripcion;
    @Column(name = "precio_base", precision = 12, scale = 2)
    public BigDecimal precioBase;
    public Integer cupoTotal;
    public Integer cupoDisponible;
    public Integer reservasActivas = 0;
    @Enumerated(EnumType.STRING)
    public EstadoPaquete estado = EstadoPaquete.ACTIVO;
    public LocalDateTime fechaCreacion = LocalDateTime.now();
    public LocalDateTime fechaActualizacion = LocalDateTime.now();
    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ActividadItinerario> itinerario = new ArrayList<>();

    public void marcarActualizacion() {
        fechaActualizacion = LocalDateTime.now();
    }

    public void reemplazarItinerario(List<ActividadItinerario> actividades) {
        itinerario.clear();
        actividades.forEach(actividad -> {
            actividad.paqueteTuristico = this;
            itinerario.add(actividad);
        });
    }
}
