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

    public record SolicitudActividadItinerario(@NotNull @Min(1) Integer numeroDia,
        @NotBlank @Size(max = 150) String titulo, @NotBlank @Size(max = 500) String descripcion,
        LocalTime horaInicio, LocalTime horaFin) {}
    public record RespuestaActividadItinerario(Integer id, Integer numeroDia, String titulo, String descripcion,
        LocalTime horaInicio, LocalTime horaFin) {}

    public record SolicitudPaqueteTuristico(@NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 80) String categoria, @NotBlank @Size(max = 120) String destino,
        @NotBlank @Size(max = 1000) String descripcion, @NotNull @DecimalMin("0.01") BigDecimal precioBase,
        @NotNull @Min(1) Integer cupoTotal, @Valid List<SolicitudActividadItinerario> itinerario) {}
    public record RespuestaPaqueteTuristico(Integer id, String nombre, String categoria, String destino,
        String descripcion, BigDecimal precioBase, Integer cupoTotal, Integer cupoDisponible,
        Integer reservasActivas, EstadoPaquete estado, LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion, List<RespuestaActividadItinerario> itinerario) {}

    public record SolicitudCupo(@NotNull @Min(1) Integer cupoTotal, @NotBlank @Size(max = 255) String motivo) {}
    public record RespuestaHistorialCupo(Integer id, Integer cupoAnterior, Integer cupoNuevo, String motivo,
        LocalDateTime fechaCambio) {}

    public record SolicitudPlanPrecio(@NotNull @Min(1) Integer idPaquete, @NotBlank @Size(max = 120) String nombre,
        @NotNull @DecimalMin("0.01") BigDecimal precio, @NotNull @Min(1) Integer cuotas,
        @NotBlank @Size(max = 500) String condiciones) {}
    public record RespuestaPlanPrecio(Integer id, Integer idPaquete, String nombre, BigDecimal precio,
        Integer cuotas, String condiciones, Boolean activo) {}

    public record SolicitudProveedor(@NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 80) String tipoProveedor, @NotBlank @Size(max = 120) String nombreContacto,
        @NotBlank @Email @Size(max = 120) String correo, @NotBlank @Size(max = 30) String telefono) {}
    public record RespuestaProveedor(Integer id, String nombre, String tipoProveedor, String nombreContacto,
        String correo, String telefono, Boolean activo) {}

    public record SolicitudSeguro(@NotNull @Min(1) Integer idPaquete, @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 500) String detalleCobertura, @NotNull @DecimalMin("0.01") BigDecimal montoCobertura) {}
    public record RespuestaSeguro(Integer id, Integer idPaquete, String nombre, String detalleCobertura,
        BigDecimal montoCobertura, Boolean activo) {}

    public record SolicitudPerfilOrganizacion(@NotBlank @Size(max = 150) String nombreOrganizacion,
        @NotBlank @Email @Size(max = 120) String correo, @NotBlank @Size(max = 30) String telefono,
        @NotBlank @Size(max = 255) String direccion, String logoBase64) {}
    public record RespuestaPerfilOrganizacion(Integer id, String nombreOrganizacion, String correo, String telefono,
        String direccion, String logoBase64, LocalDateTime fechaActualizacion) {}

    public record RespuestaErrorApi(LocalDateTime fecha, int estado, String error, String mensaje,
        String ruta, Map<String, String> campos) {}
}
