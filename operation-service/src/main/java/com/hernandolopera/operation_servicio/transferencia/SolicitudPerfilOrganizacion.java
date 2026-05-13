package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudPerfilOrganizacion(
    @NotBlank @Size(max = 150) String nombreOrganizacion,
    @NotBlank @Email @Size(max = 120) String correo,
    @NotBlank @Size(max = 30) String telefono,
    @NotBlank @Size(max = 255) String direccion,
    String logoBase64
) {
}
