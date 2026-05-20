package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "provider")
public class ProveedorTuristico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_provider")
    public Integer id;

    @Column(name = "name", nullable = false, length = 100)
    public String nombre;

    @Column(name = "type", nullable = false, length = 100)
    public String tipoProveedor;

    @Column(name = "email", length = 150)
    public String correo;

    @Column(name = "phone", length = 20)
    public String telefono;

    @Column(name = "status", nullable = false)
    public Boolean activo = true;
}
