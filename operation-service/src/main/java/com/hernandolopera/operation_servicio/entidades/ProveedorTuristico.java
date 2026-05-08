package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedor_turistico")
public class ProveedorTuristico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    public Integer id;
    public String nombre;
    public String tipoProveedor;
    public String nombreContacto;
    public String correo;
    public String telefono;
    public Boolean activo = true;
}
