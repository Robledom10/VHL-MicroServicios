package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_comment")
public class ComentarioPaquete {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment")
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_package", nullable = false)
    public PaqueteTuristico paqueteTuristico;

    @Column(name = "comment", nullable = false, length = 500)
    public String comentario;

    @Column(name = "score", nullable = false)
    public Integer puntaje;

    @Column(name = "author", length = 100)
    public String autor;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime fechaCreacion = LocalDateTime.now();
}
