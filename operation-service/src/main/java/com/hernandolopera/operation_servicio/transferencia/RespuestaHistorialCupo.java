package com.hernandolopera.operation_servicio.transferencia;

import java.time.LocalDateTime;

public record RespuestaHistorialCupo(
    Integer id,
    Integer cupoAnterior,
    Integer cupoNuevo,
    String motivo,
    LocalDateTime fechaCambio
) {
}
