package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record SolicitudActividadItinerario(
    @NotNull @Min(1) Integer numeroDia,
    @NotBlank @Size(max = 150) String titulo,
    @NotBlank @Size(max = 500) String descripcion,
    LocalTime horaInicio,
    LocalTime horaFin
) {
}
