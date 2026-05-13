package com.hernandolopera.operation_servicio.transferencia;

public record RespuestaProveedor(
    Integer id,
    String nombre,
    String tipoProveedor,
    String nombreContacto,
    String correo,
    String telefono,
    Boolean activo
) {
}
