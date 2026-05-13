package com.hernandolopera.reservation_service.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long id;

    @Column(name = "reservation_code", unique = true, nullable = false, length = 50)
    private String numeroReserva;

    @Column(name = "package_id", nullable = false)
    private Long idPaquete;

    @Column(name = "id_user", nullable = false)
    private Long idUsuario;

    @Column(name = "passenger_count")
    private Integer cantidadPasajeros;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioTotal;

    @Column(name = "travel_start_date")
    private LocalDateTime fechaInicioViaje;

    @Column(name = "travel_end_date")
    private LocalDateTime fechaFinViaje;

    @Convert(converter = EstadoReservaConverter.class)
    @Column(name = "status", nullable = false)
    private EstadoReserva estado;

    @Column(name = "payment_verified")
    private Boolean pagoVerificado;

    @Column(name = "reservation_date", nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "updated_at")
    private LocalDateTime fechaActualizacion;

    @Column(name = "confirmation_date")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "notes", length = 500)
    private String notas;
}
