package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trip_departure")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalidaViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trip")
    private Long id;

    @Column(name = "id_user", nullable = false)
    private Long idUsuario;

    @Column(name = "id_package", nullable = false)
    private Long idPaquete;

    @Column(name = "departure_date", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "return_date", nullable = false)
    private LocalDate fechaRegreso;

    @Column(name = "status", nullable = false, length = 30)
    private String estado;
}
