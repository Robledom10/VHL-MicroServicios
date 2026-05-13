package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfil_organizacion")
public class PerfilOrganizacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    public Integer id;
    public String nombreOrganizacion;
    public String correo;
    public String telefono;
    public String direccion;
    @Lob
    public String logoBase64;
    public LocalDateTime fechaActualizacion = LocalDateTime.now();
}
