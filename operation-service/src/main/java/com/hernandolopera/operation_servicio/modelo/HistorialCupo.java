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
import java.time.LocalDateTime;

@Entity
@Table(name = "capacity_historial")
public class HistorialCupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_capacity_historial")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete", nullable = false)
    private PaqueteTuristico paqueteTuristico;

    @Column(nullable = false)
    private Integer cupoAnterior;

    @Column(nullable = false)
    private Integer cupoNuevo;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaCambio = LocalDateTime.now();

    public Integer getId() {
        return id;
    }

    public PaqueteTuristico getPaqueteTuristico() {
        return paqueteTuristico;
    }

    public void setPaqueteTuristico(PaqueteTuristico paqueteTuristico) {
        this.paqueteTuristico = paqueteTuristico;
    }

    public Integer getCupoAnterior() {
        return cupoAnterior;
    }

    public void setCupoAnterior(Integer cupoAnterior) {
        this.cupoAnterior = cupoAnterior;
    }

    public Integer getCupoNuevo() {
        return cupoNuevo;
    }

    public void setCupoNuevo(Integer cupoNuevo) {
        this.cupoNuevo = cupoNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
}
