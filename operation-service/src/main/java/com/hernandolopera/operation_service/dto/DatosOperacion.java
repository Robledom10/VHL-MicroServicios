package com.hernandolopera.operation_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class DatosOperacion {
    private DatosOperacion() {
    }

    public record SolicitudViaje(
        @NotNull @Min(1) Long idUsuario,
        @NotNull @Min(1) Long idPaquete,
        @NotNull LocalDate fechaSalida,
        @NotNull LocalDate fechaRegreso
    ) {
    }

    public record RespuestaViaje(
        Long id,
        Long idUsuario,
        Long idPaquete,
        LocalDate fechaSalida,
        LocalDate fechaRegreso,
        String estado,
        String mensaje
    ) {
    }

    public record SolicitudTransporte(
        @NotBlank String tipoTransporte,
        @NotBlank String empresa,
        @NotBlank String placa,
        @NotBlank String conductor,
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,20}$", message = "El telefono del conductor no tiene un formato valido")
        String telefonoConductor,
        @NotNull @Min(1) Integer capacidad,
        @NotNull @Min(1) Integer cantidadViajeros,
        @NotNull LocalDateTime fechaSalida
    ) {
    }

    public record RespuestaTransporte(
        Long id,
        Long idViaje,
        String tipoTransporte,
        String empresa,
        String placa,
        String conductor,
        String telefonoConductor,
        Integer capacidad,
        Integer cantidadViajeros,
        LocalDateTime fechaSalida,
        LocalDateTime fechaRegistro,
        String mensaje
    ) {
    }

    public record SolicitudCheckIn(
        @NotNull Long idViajero,
        @NotBlank String codigoQr,
        Long idReserva
    ) {
    }

    public record RespuestaCheckIn(
        Long id,
        Long idViaje,
        Long idViajero,
        String codigoQr,
        Long idReserva,
        LocalDateTime fechaCheckIn,
        String mensaje
    ) {
    }

    public record SolicitudAlojamiento(
        @NotNull Long idViajero,
        String nombreViajero,
        @NotBlank String hotel,
        @NotBlank String habitacion,
        @NotBlank String direccion,
        @NotNull LocalDate fechaIngreso,
        @NotNull LocalDate fechaSalida
    ) {
    }

    public record RespuestaAlojamiento(
        Long id,
        Long idViaje,
        Long idViajero,
        String nombreViajero,
        String hotel,
        String habitacion,
        String direccion,
        LocalDate fechaIngreso,
        LocalDate fechaSalida,
        LocalDateTime fechaRegistro,
        String mensaje
    ) {
    }

    public record SolicitudInformacionMedica(
        @NotNull Long idViaje,
        @NotBlank String tipoSangre,
        @Size(max = 500) String alergias,
        @Size(max = 500) String medicamentos,
        @Size(max = 500) String condicionesMedicas,
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,20}$", message = "El telefono medico no tiene un formato valido")
        String telefonoMedico,
        String nombreViajero
    ) {
    }

    public record RespuestaInformacionMedica(
        Long id,
        Long idViaje,
        Long idViajero,
        String tipoSangre,
        String alergias,
        String medicamentos,
        String condicionesMedicas,
        String telefonoMedico,
        LocalDateTime fechaRegistro,
        String mensaje,
        String nombreViajero
    ) {
    }

    public record SolicitudContactoEmergencia(
        @NotNull Long idViaje,
        @NotBlank String nombre,
        @NotBlank String parentesco,
        @Pattern(regexp = "^\\+?[0-9\\s-]{7,20}$", message = "El telefono no tiene un formato valido")
        @NotBlank String telefono,
        @Email String correo,
        String nombreViajero
    ) {
    }

    public record SolicitudContactoDesdeReserva(
        @NotBlank String nombre,
        @NotBlank String parentesco,
        @NotBlank String telefono,
        String correo,
        String nombreViajero
    ) {
    }

    public record RespuestaContactoEmergencia(
        Long id,
        Long idViaje,
        Long idViajero,
        String nombre,
        String parentesco,
        String telefono,
        String correo,
        LocalDateTime fechaRegistro,
        String mensaje,
        String nombreViajero
    ) {
    }

    public record SolicitudRestaurante(
        @NotBlank String nombreRestaurante,
        String direccion,
        String telefono,
        String tipoComida,
        @Size(max = 500) String notas
    ) {
    }

    public record RespuestaRestaurante(
        Long id,
        Long idViaje,
        String nombreRestaurante,
        String direccion,
        String telefono,
        String tipoComida,
        String notas,
        LocalDateTime fechaRegistro,
        String mensaje
    ) {
    }

    public record SolicitudIncidente(
        @NotBlank String tipo,
        @NotBlank @Size(max = 255) String descripcion,
        @NotBlank String severidad,
        Long idViajero,
        @NotBlank String reportadoPor
    ) {
    }

    public record RespuestaIncidente(
        Long id,
        Long idViaje,
        Long idViajero,
        String tipo,
        String descripcion,
        String severidad,
        String estado,
        String reportadoPor,
        LocalDateTime fechaRegistro,
        String mensaje
    ) {
    }

    public record SolicitudNotificacion(
        @NotBlank String asunto,
        @NotBlank @Size(max = 1000) String mensaje,
        @NotBlank String canal,
        List<Long> destinatarios,
        List<String> contactos
    ) {
    }

    public record RespuestaNotificacion(
        Long id,
        Long idViaje,
        String asunto,
        String mensaje,
        String canal,
        Integer totalDestinatarios,
        LocalDateTime fechaEnvio,
        String estado,
        String respuesta
    ) {
    }

    public record RespuestaEmailDTO(
        Long id,
        Long notificacionId,
        String remitenteEmail,
        String asunto,
        String contenido,
        LocalDateTime fechaRecibida,
        boolean leida
    ) {
    }

    public record RespuestaDashboard(
        Long idViaje,
        Integer viajerosRegistrados,
        Integer viajerosConCheckIn,
        Integer transportesAsignados,
        Integer alojamientosAsignados,
        Integer incidentesRegistrados,
        Integer incidentesAbiertos,
        Integer notificacionesEnviadas,
        Double porcentajeCheckIn,
        LocalDateTime fechaConsulta
    ) {
    }

    public record RespuestaErrorApi(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> campos
    ) {
    }

    public record SolicitudActualizarEstadoIncidente(
        @NotBlank String estado
    ) {
    }

    public record RespuestaOperacion(
        String mensaje,
        Object datos
    ) {
    }
}
