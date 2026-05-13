package com.hernandolopera.operation_servicio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "itinerario_actividad")
public class ActividadItinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete", nullable = false)
    private PaqueteTuristico paqueteTuristico;

    @Column(nullable = false)
    private Integer numeroDia;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    public Integer getId() {
        return id;
    }

    public PaqueteTuristico getPaqueteTuristico() {
        return paqueteTuristico;
    }

    public void setPaqueteTuristico(PaqueteTuristico paqueteTuristico) {
        this.paqueteTuristico = paqueteTuristico;
    }

    public Integer getDayNumber() {
        return numeroDia;
    }

    public void setDayNumber(Integer numeroDia) {
        this.numeroDia = numeroDia;
    }

    public String getTitle() {
        return titulo;
    }

    public void setTitle(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalTime getStartTime() {
        return horaInicio;
    }

    public void setStartTime(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getEndTime() {
        return horaFin;
    }

    public void setEndTime(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
