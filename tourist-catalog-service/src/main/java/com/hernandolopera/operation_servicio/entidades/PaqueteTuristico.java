package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`package`")
public class PaqueteTuristico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_package")
    public Integer id;

    @Column(name = "title", nullable = false, length = 150)
    public String titulo;

    @Column(name = "description", length = 255)
    public String descripcion;

    @Column(name = "destination", nullable = false, length = 150)
    public String destino;

    @Column(name = "duration_days", nullable = false)
    public Integer duracionDias;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    public BigDecimal precio;

    @Column(name = "quota", nullable = false)
    public Integer cupo;

    @Column(name = "start_date", nullable = false)
    public LocalDate fechaInicio;

    @Column(name = "end_date", nullable = false)
    public LocalDate fechaFin;

    @Column(name = "status", nullable = false)
    public Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_category", nullable = false)
    public Categoria categoria;

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ActividadItinerario> itinerario = new ArrayList<>();

    public void reemplazarItinerario(List<ActividadItinerario> actividades) {
        itinerario.clear();
        actividades.forEach(actividad -> {
            actividad.paqueteTuristico = this;
            itinerario.add(actividad);
        });
    }
}
