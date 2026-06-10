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

    // Transporte
    @Column(name = "tipo_vehiculo", length = 100)
    public String tipoVehiculo;

    @Column(name = "placa", length = 20)
    public String placa;

    @Column(name = "conductor", length = 100)
    public String conductor;

    @Column(name = "telefono_conductor", length = 20)
    public String telefonoConductor;

    @Column(name = "capacidad")
    public Integer capacidad;

    // Hotel / Restaurante
    @Column(name = "direccion", length = 200)
    public String direccion;

    // Guía
    @Column(name = "especialidad", length = 100)
    public String especialidad;

    @Column(name = "idioma", length = 100)
    public String idioma;

    // Restaurante
    @Column(name = "tipo_comida", length = 100)
    public String tipoComida;

    // General
    @Column(name = "notas", length = 500)
    public String notas;
}
