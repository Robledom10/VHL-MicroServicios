package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transport_assignment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransporteAsignado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "vehicle_type", nullable = false, length = 100)
    private String tipoTransporte;

    @Column(name = "company", nullable = false, length = 100)
    private String empresa;

    @Column(name = "plate", nullable = false, length = 20)
    private String placa;

    @Column(name = "driver_name", length = 120)
    private String conductor;

    @Column(name = "driver_phone", length = 30)
    private String telefonoConductor;

    @Column(name = "capacity", nullable = false)
    private Integer capacidad;

    @Column(name = "traveler_count", nullable = false)
    private Integer cantidadViajeros;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime fechaSalida;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
