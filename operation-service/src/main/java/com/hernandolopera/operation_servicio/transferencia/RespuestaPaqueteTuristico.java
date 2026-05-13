package com.hernandolopera.operation_servicio.transferencia;

import com.hernandolopera.operation_servicio.modelo.EstadoPaquete;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RespuestaPaqueteTuristico(
    Integer id,
    String nombre,
    String categoria,
    String destino,
    String descripcion,
    BigDecimal precioBase,
    Integer cupoTotal,
    Integer cupoDisponible,
    Integer reservasActivas,
    EstadoPaquete estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion,
    List<RespuestaActividadItinerario> itinerario
) {
}
