package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lodging_assignment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoAsignado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lodging")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "id_traveler")
    private Long idViajero;

    @Column(name = "hotel_name", nullable = false, length = 150)
    private String hotel;

    @Column(name = "room_number", length = 60)
    private String habitacion;

    @Column(name = "address", nullable = false, length = 255)
    private String direccion;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
