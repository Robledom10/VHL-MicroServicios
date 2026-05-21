package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class DatosOperacion {
    private DatosOperacion() {}

    public record SolicitudCategoria(@NotBlank String nombre, String descripcion) {}
    public record RespuestaCategoria(Integer id, String nombre, String descripcion, Boolean activo) {}

    public record SolicitudActividadItinerario(@NotNull @Min(1) Integer numeroDia, @NotBlank String titulo,
        String descripcion) {}
    public record RespuestaActividadItinerario(Integer id, Integer numeroDia, String titulo, String descripcion,
        Integer idPaquete) {}

    public record SolicitudPaqueteTuristico(@NotBlank String titulo, String descripcion, @NotBlank String destino,
        @NotNull @Min(1) Integer duracionDias, @NotNull @DecimalMin("0.01") BigDecimal precio,
        @NotNull @Min(1) Integer cupo, @NotNull LocalDate fechaInicio, @NotNull LocalDate fechaFin,
        @NotNull Integer idCategoria, @Valid List<SolicitudActividadItinerario> itinerario) {}
    public record RespuestaPaqueteTuristico(Integer id, String titulo, String descripcion, String destino,
        Integer duracionDias, BigDecimal precio, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin,
        Boolean activo, Integer idCategoria, String categoria, List<RespuestaActividadItinerario> itinerario) {}

    public record SolicitudProveedor(@NotBlank String nombre, @NotBlank String tipoProveedor,
        @Email String correo, String telefono) {}
    public record RespuestaProveedor(Integer id, String nombre, String tipoProveedor,
        String correo, String telefono, Boolean activo) {}

    public record RespuestaErrorApi(LocalDateTime fecha, int estado, String error, String mensaje,
        String ruta, Map<String, String> campos) {}
}
