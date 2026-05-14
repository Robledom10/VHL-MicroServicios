package com.hernandolopera.operation_servicio.transferencia;

import java.math.BigDecimal;

public record RespuestaSeguro(
    Integer id,
    Integer idPaquete,
    String nombre,
    String detalleCobertura,
    BigDecimal montoCobertura,
    Boolean activo
) {
}
