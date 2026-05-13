package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudProveedor(
    @NotBlank @Size(max = 150) String nombre,
    @NotBlank @Size(max = 80) String tipoProveedor,
    @NotBlank @Size(max = 120) String nombreContacto,
    @NotBlank @Email @Size(max = 120) String correo,
    @NotBlank @Size(max = 30) String telefono
) {
}
