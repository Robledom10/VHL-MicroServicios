package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record SolicitudPaqueteTuristico(
    @NotBlank @Size(max = 150) String nombre,
    @NotBlank @Size(max = 80) String categoria,
    @NotBlank @Size(max = 120) String destino,
    @NotBlank @Size(max = 1000) String descripcion,
    @NotNull @DecimalMin("0.01") BigDecimal precioBase,
    @NotNull @Min(1) Integer cupoTotal,
    @Valid List<SolicitudActividadItinerario> itinerario
) {
}
