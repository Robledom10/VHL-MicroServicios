package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SolicitudSeguro(
    @NotNull Integer idPaquete,
    @NotBlank @Size(max = 120) String nombre,
    @NotBlank @Size(max = 500) String detalleCobertura,
    @NotNull @DecimalMin("0.01") BigDecimal montoCobertura
) {
}
