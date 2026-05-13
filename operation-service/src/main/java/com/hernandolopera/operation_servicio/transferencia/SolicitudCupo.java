package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudCupo(
    @NotNull @Min(1) Integer cupoTotal,
    @NotBlank @Size(max = 255) String motivo
) {
}
