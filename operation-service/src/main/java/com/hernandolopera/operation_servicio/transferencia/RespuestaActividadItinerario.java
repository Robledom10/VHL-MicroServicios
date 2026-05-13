package com.hernandolopera.operation_servicio.transferencia;

import java.time.LocalTime;

public record RespuestaActividadItinerario(
    Integer id,
    Integer numeroDia,
    String titulo,
    String descripcion,
    LocalTime horaInicio,
    LocalTime horaFin
) {
}
