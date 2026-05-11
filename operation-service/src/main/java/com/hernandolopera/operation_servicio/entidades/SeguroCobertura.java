package com.hernandolopera.operation_servicio.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seguro_cobertura")
public class SeguroCobertura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguro")
    public Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_paquete")
    public PaqueteTuristico paqueteTuristico;
    public String nombre;
    @Column(length = 500)
    public String detalleCobertura;
    @Column(precision = 12, scale = 2)
    public BigDecimal montoCobertura;
    public Boolean activo = true;
}
