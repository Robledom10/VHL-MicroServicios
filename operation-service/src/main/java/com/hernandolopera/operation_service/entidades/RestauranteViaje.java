package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "restaurant_assignment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RestauranteViaje {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restaurant")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "restaurant_name", nullable = false, length = 150)
    private String nombreRestaurante;

    @Column(name = "address", length = 255)
    private String direccion;

    @Column(name = "phone", length = 30)
    private String telefono;

    @Column(name = "meal_type", length = 100)
    private String tipoComida;

    @Column(name = "notes", length = 500)
    private String notas;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
