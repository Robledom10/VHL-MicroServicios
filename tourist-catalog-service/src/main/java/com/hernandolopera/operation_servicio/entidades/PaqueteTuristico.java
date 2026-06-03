package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @ElementCollection
    @CollectionTable(name = "package_destination", joinColumns = @JoinColumn(name = "fk_id_package"))
    @Column(name = "destination", nullable = false, length = 150)
    public List<String> destinos = new ArrayList<>();

    @Column(name = "duration_days", nullable = false)
    public Integer duracionDias;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    public BigDecimal precio;

    @Column(name = "quota", nullable = false)
    public Integer cupo;

    @Column(name = "start_date", nullable = false)
    public LocalDate fechaInicio;

    @Column(name = "departure_place", length = 150)
    public String lugarSalida;

    @Column(name = "departure_time")
    public LocalTime horaSalida;

    @Column(name = "lodging", length = 150)
    public String alojamiento;

    @Column(name = "room_type", length = 120)
    public String tipoHabitacion;

    @Column(name = "transport_type", length = 80)
    public String tipoTransporte;

    @ElementCollection
    @CollectionTable(name = "package_transport_type", joinColumns = @JoinColumn(name = "fk_id_package"))
    @Column(name = "transport_type", nullable = false, length = 80)
    public List<String> tiposTransporte = new ArrayList<>();

    @Column(name = "vertical_photo_url", length = 500)
    public String fotoVerticalUrl;

    @Column(name = "horizontal_photo_url", length = 500)
    public String fotoHorizontalUrl;

    @Column(name = "status", nullable = false)
    public Boolean activo = true;

    @OneToMany(mappedBy = "paqueteTuristico", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ActividadItinerario> itinerario = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_inclusion", joinColumns = @JoinColumn(name = "fk_id_package"))
    @Column(name = "description", nullable = false, length = 150)
    public List<String> incluye = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_exclusion", joinColumns = @JoinColumn(name = "fk_id_package"))
    @Column(name = "description", nullable = false, length = 150)
    public List<String> noIncluye = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_cancellation_policy", joinColumns = @JoinColumn(name = "fk_id_package"))
    @Column(name = "description", nullable = false, length = 255)
    public List<String> politicasCancelacion = new ArrayList<>();

    public void reemplazarItinerario(List<ActividadItinerario> actividades) {
        itinerario.clear();
        actividades.forEach(actividad -> {
            actividad.paqueteTuristico = this;
            itinerario.add(actividad);
        });
    }

    public void reemplazarListasDelDetalle(List<String> incluye, List<String> noIncluye, List<String> politicasCancelacion) {
        this.incluye.clear();
        this.incluye.addAll(incluye);
        this.noIncluye.clear();
        this.noIncluye.addAll(noIncluye);
        this.politicasCancelacion.clear();
        this.politicasCancelacion.addAll(politicasCancelacion);
    }

    public void reemplazarDestinos(List<String> destinos) {
        this.destinos.clear();
        this.destinos.addAll(destinos);
        this.destino = destinos.getFirst();
    }

    public void reemplazarTiposTransporte(List<String> tiposTransporte) {
        this.tiposTransporte.clear();
        this.tiposTransporte.addAll(tiposTransporte);
        this.tipoTransporte = String.join(", ", tiposTransporte);
    }
}
