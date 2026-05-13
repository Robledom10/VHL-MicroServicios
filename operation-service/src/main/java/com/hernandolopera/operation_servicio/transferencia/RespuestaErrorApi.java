package com.hernandolopera.operation_servicio.transferencia;

import java.time.LocalDateTime;
import java.util.Map;

public record RespuestaErrorApi(
    LocalDateTime timestamp,
    int estado,
    String error,
    String message,
    String path,
    Map<String, String> fields
) {
}
