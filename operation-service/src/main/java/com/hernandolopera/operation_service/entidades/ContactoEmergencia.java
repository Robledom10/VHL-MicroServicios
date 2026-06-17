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
@Table(name = "emergency_contact")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoEmergencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contact")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "id_traveler")
    private Long idViajero;

    @Column(name = "full_name", nullable = false, length = 120)
    private String nombre;

    @Column(name = "relationship", nullable = false, length = 80)
    private String parentesco;

    @Column(name = "phone", nullable = false, length = 30)
    private String telefono;

    @Column(name = "email", length = 150)
    private String correo;

    @Column(name = "traveler_name", length = 150)
    private String nombreViajero;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
