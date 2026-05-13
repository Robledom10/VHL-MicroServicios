package com.hernandolopera.operation_servicio.transferencia;

import com.hernandolopera.operation_servicio.entidades.EstadoPaquete;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public final class DatosOperacion {
    private DatosOperacion() {}

    public record SolicitudActividadItinerario(@NotNull @Min(1) Integer numeroDia, @NotBlank String titulo,
        @NotBlank String descripcion, LocalTime horaInicio, LocalTime horaFin) {}
    public record RespuestaActividadItinerario(Integer id, Integer numeroDia, String titulo, String descripcion,
        LocalTime horaInicio, LocalTime horaFin) {}

    public record SolicitudPaqueteTuristico(@NotBlank String nombre, @NotBlank String categoria, @NotBlank String destino,
        @NotBlank String descripcion, @NotNull @DecimalMin("0.01") BigDecimal precioBase,
        @NotNull @Min(1) Integer cupoTotal, @Valid List<SolicitudActividadItinerario> itinerario) {}
    public record RespuestaPaqueteTuristico(Integer id, String nombre, String categoria, String destino,
        String descripcion, BigDecimal precioBase, Integer cupoTotal, Integer cupoDisponible,
        Integer reservasActivas, EstadoPaquete estado, LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion, List<RespuestaActividadItinerario> itinerario) {}

    public record SolicitudCupo(@NotNull @Min(1) Integer cupoTotal, @NotBlank String motivo) {}
    public record RespuestaHistorialCupo(Integer id, Integer cupoAnterior, Integer cupoNuevo, String motivo,
        LocalDateTime fechaCambio) {}

    public record SolicitudPlanPrecio(@NotNull Integer idPaquete, @NotBlank String nombre,
        @NotNull @DecimalMin("0.01") BigDecimal precio, @NotNull @Min(1) Integer cuotas,
        @NotBlank String condiciones) {}
    public record RespuestaPlanPrecio(Integer id, Integer idPaquete, String nombre, BigDecimal precio,
        Integer cuotas, String condiciones, Boolean activo) {}

    public record SolicitudProveedor(@NotBlank String nombre, @NotBlank String tipoProveedor,
        @NotBlank String nombreContacto, @NotBlank @Email String correo, @NotBlank String telefono) {}
    public record RespuestaProveedor(Integer id, String nombre, String tipoProveedor, String nombreContacto,
        String correo, String telefono, Boolean activo) {}

    public record SolicitudSeguro(@NotNull Integer idPaquete, @NotBlank String nombre,
        @NotBlank String detalleCobertura, @NotNull @DecimalMin("0.01") BigDecimal montoCobertura) {}
    public record RespuestaSeguro(Integer id, Integer idPaquete, String nombre, String detalleCobertura,
        BigDecimal montoCobertura, Boolean activo) {}

    public record SolicitudPerfilOrganizacion(@NotBlank String nombreOrganizacion, @NotBlank @Email String correo,
        @NotBlank String telefono, @NotBlank String direccion, String logoBase64) {}
    public record RespuestaPerfilOrganizacion(Integer id, String nombreOrganizacion, String correo, String telefono,
        String direccion, String logoBase64, LocalDateTime fechaActualizacion) {}

    public record RespuestaErrorApi(LocalDateTime fecha, int estado, String error, String mensaje,
        String ruta, Map<String, String> campos) {}
}
