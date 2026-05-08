package com.hernandolopera.operation_servicio.transferencia;

import java.time.LocalDateTime;

public record RespuestaPerfilOrganizacion(
    Integer id,
    String nombreOrganizacion,
    String correo,
    String telefono,
    String direccion,
    String logoBase64,
    LocalDateTime fechaActualizacion
) {
}
