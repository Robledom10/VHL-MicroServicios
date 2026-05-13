package com.hernandolopera.operation_servicio.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "paquete_turistico")
public class PaqueteTuristico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private Integer cupoTotal;

    @Column(nullable = false)
    private Integer cupoDisponible;

    @Column(nullable = false)
    private Integer reservasActivas = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPaquete estado = EstadoPaquete.ACTIVO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActividadItinerario> itinerario = new ArrayList<>();

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPrecio> planPrecios = new ArrayList<>();

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeguroCobertura> seguros = new ArrayList<>();

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialCupo> historialCupos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "paquete_proveedor",
        joinColumns = @JoinColumn(name = "fk_id_paquete"),
        inverseJoinColumns = @JoinColumn(name = "fk_id_proveedor")
    )
    private Set<ProveedorTuristico> proveedores = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public Integer getCupoTotal() {
        return cupoTotal;
    }

    public void setCupoTotal(Integer cupoTotal) {
        this.cupoTotal = cupoTotal;
    }

    public Integer getCupoDisponible() {
        return cupoDisponible;
    }

    public void setCupoDisponible(Integer cupoDisponible) {
        this.cupoDisponible = cupoDisponible;
    }

    public Integer getReservasActivas() {
        return reservasActivas;
    }

    public void setReservasActivas(Integer reservasActivas) {
        this.reservasActivas = reservasActivas;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public void setEstado(EstadoPaquete estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void marcarActualizacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public List<ActividadItinerario> getItinerario() {
        return itinerario;
    }

    public void reemplazarItinerario(List<ActividadItinerario> actividades) {
        itinerario.clear();
        actividades.forEach(actividad -> {
            actividad.setPaqueteTuristico(this);
            itinerario.add(actividad);
        });
    }

    public Set<ProveedorTuristico> getProveedores() {
        return proveedores;
    }
}
