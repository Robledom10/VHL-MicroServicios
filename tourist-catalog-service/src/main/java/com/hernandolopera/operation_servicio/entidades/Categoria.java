package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorie")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    public Integer id;

    @Column(name = "name", nullable = false, length = 100)
    public String nombre;

    @Column(name = "description", length = 150)
    public String descripcion;

    @Column(name = "status", nullable = false)
    public Boolean activo = true;
}
