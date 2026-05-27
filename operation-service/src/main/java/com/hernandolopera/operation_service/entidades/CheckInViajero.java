package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "traveler_check_in",
    uniqueConstraints = @UniqueConstraint(name = "uk_check_in_trip_traveler", columnNames = {"fk_id_trip", "id_traveler"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInViajero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_check_in")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "id_traveler", nullable = false)
    private Long idViajero;

    @Column(name = "qr_code", nullable = false, length = 180)
    private String codigoQr;

    @Column(name = "id_reservation")
    private Long idReserva;

    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime fechaCheckIn;
}
