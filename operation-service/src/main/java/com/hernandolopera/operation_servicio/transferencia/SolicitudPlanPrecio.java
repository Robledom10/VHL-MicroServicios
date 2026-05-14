package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SolicitudPlanPrecio(
    @NotNull Integer idPaquete,
    @NotBlank @Size(max = 120) String nombre,
    @NotNull @DecimalMin("0.01") BigDecimal precio,
    @NotNull @Min(1) Integer cuotas,
    @NotBlank @Size(max = 500) String condiciones
) {
}
