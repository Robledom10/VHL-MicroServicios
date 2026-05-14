package com.hernandolopera.operation_servicio.transferencia;

import java.math.BigDecimal;

public record RespuestaPlanPrecio(
    Integer id,
    Integer idPaquete,
    String nombre,
    BigDecimal precio,
    Integer cuotas,
    String condiciones,
    Boolean activo
) {
}
