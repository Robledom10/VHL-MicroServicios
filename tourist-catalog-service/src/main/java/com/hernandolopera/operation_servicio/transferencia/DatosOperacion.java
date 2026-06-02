package com.hernandolopera.operation_servicio.transferencia;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public final class DatosOperacion {
    private DatosOperacion() {}

    public record SolicitudActividadItinerario(@NotNull @Min(1) Integer numeroDia, @NotBlank String titulo) {}
    public record RespuestaActividadItinerario(Integer id, Integer numeroDia, String titulo, Integer idPaquete) {}

    public record SolicitudPaqueteTuristico(@NotBlank String titulo, String descripcion, String destino,
        List<@NotBlank String> destinos,
        @NotNull @Min(1) Integer duracionDias, @NotNull @DecimalMin("0.01") BigDecimal precio,
        @NotNull @Min(1) Integer cupo, @NotNull LocalDate fechaInicio,
        String lugarSalida, LocalTime horaSalida, String alojamiento, String tipoHabitacion, String tipoTransporte,
        List<@NotBlank String> tiposTransporte,
        String fotoVerticalUrl, String fotoHorizontalUrl, List<@NotBlank String> incluye,
        List<@NotBlank String> noIncluye, List<@NotBlank String> politicasCancelacion,
        @Valid List<SolicitudActividadItinerario> itinerario) {}
    public record RespuestaPaqueteTuristico(Integer id, String titulo, String descripcion, String destino,
        List<String> destinos,
        Integer duracionDias, BigDecimal precio, Integer cupo, LocalDate fechaInicio,
        String lugarSalida, LocalTime horaSalida, String alojamiento, String tipoHabitacion, String tipoTransporte,
        List<String> tiposTransporte,
        String fotoVerticalUrl, String fotoHorizontalUrl, List<String> incluye, List<String> noIncluye,
        List<String> politicasCancelacion, Boolean activo, List<RespuestaActividadItinerario> itinerario) {}
    public record RespuestaImagenPaquete(String url) {}

    public record SolicitudComentarioPaquete(@NotBlank String comentario, @NotNull @Min(1) @Max(5) Integer puntaje) {}
    public record RespuestaComentarioPaquete(Integer id, Integer idPaquete, String comentario, Integer puntaje,
        String autor, LocalDateTime fechaCreacion) {}

    public record SolicitudProveedor(@NotBlank String nombre, @NotBlank String tipoProveedor,
        @Email String correo, String telefono) {}
    public record RespuestaProveedor(Integer id, String nombre, String tipoProveedor,
        String correo, String telefono, Boolean activo) {}

    public record RespuestaErrorApi(LocalDateTime fecha, int estado, String error, String mensaje,
        String ruta, Map<String, String> campos) {}
}
