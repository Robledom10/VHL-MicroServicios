package com.hernandolopera.operation_service.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "guide_assignment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GuiaAsignado {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guide")
    private Long id;

    @Column(name = "fk_id_trip", nullable = false)
    private Long idViaje;

    @Column(name = "guide_name", nullable = false, length = 150)
    private String nombreGuia;

    @Column(name = "phone", length = 30)
    private String telefono;

    @Column(name = "email", length = 150)
    private String correo;

    @Column(name = "specialty", length = 200)
    private String especialidad;

    @Column(name = "language", length = 100)
    private String idioma;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaRegistro;
}
