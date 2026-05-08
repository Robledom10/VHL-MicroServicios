package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plan_precio")
public class PlanPrecio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan_precio")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete")
    public PaqueteTuristico paqueteTuristico;
    public String nombre;
    @Column(precision = 12, scale = 2)
    public BigDecimal precio;
    public Integer cuotas;
    @Column(length = 500)
    public String condiciones;
    public Boolean activo = true;
}
