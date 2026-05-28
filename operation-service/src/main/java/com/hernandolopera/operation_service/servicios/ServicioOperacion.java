package com.hernandolopera.operation_service.servicios;

import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaAlojamiento;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaCheckIn;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaContactoEmergencia;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaDashboard;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaInformacionMedica;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaNotificacion;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaTransporte;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaViaje;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudAlojamiento;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudCheckIn;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudContactoEmergencia;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudInformacionMedica;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudNotificacion;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudTransporte;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudViaje;
import com.hernandolopera.operation_service.entidades.AlojamientoAsignado;
import com.hernandolopera.operation_service.entidades.CheckInViajero;
import com.hernandolopera.operation_service.entidades.ContactoEmergencia;
import com.hernandolopera.operation_service.entidades.IncidenteViaje;
import com.hernandolopera.operation_service.entidades.InformacionMedica;
import com.hernandolopera.operation_service.entidades.NotificacionViaje;
import com.hernandolopera.operation_service.entidades.SalidaViaje;
import com.hernandolopera.operation_service.entidades.TransporteAsignado;
import com.hernandolopera.operation_service.excepciones.ExcepcionReglaNegocio;
import com.hernandolopera.operation_service.repositorios.RepositorioAlojamientoAsignado;
import com.hernandolopera.operation_service.repositorios.RepositorioCheckInViajero;
import com.hernandolopera.operation_service.repositorios.RepositorioContactoEmergencia;
import com.hernandolopera.operation_service.repositorios.RepositorioIncidenteViaje;
import com.hernandolopera.operation_service.repositorios.RepositorioInformacionMedica;
import com.hernandolopera.operation_service.repositorios.RepositorioNotificacionViaje;
import com.hernandolopera.operation_service.repositorios.RepositorioSalidaViaje;
import com.hernandolopera.operation_service.repositorios.RepositorioTransporteAsignado;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServicioOperacion {
    private static final String ESTADO_VIAJE_PROGRAMADO = "programado";
    private static final String ESTADO_INCIDENTE_PENDIENTE = "pendiente";
    private static final String ESTADO_ENVIADA = "ENVIADA";

    private final RepositorioSalidaViaje repositorioSalidaViaje;
    private final RepositorioTransporteAsignado repositorioTransporte;
    private final RepositorioCheckInViajero repositorioCheckIn;
    private final RepositorioAlojamientoAsignado repositorioAlojamiento;
    private final RepositorioInformacionMedica repositorioInformacionMedica;
    private final RepositorioContactoEmergencia repositorioContactoEmergencia;
    private final RepositorioIncidenteViaje repositorioIncidente;
    private final RepositorioNotificacionViaje repositorioNotificacion;

    public RespuestaViaje crearViaje(SolicitudViaje solicitud) {
        if (!solicitud.fechaRegreso().isAfter(solicitud.fechaSalida())) {
            throw new ExcepcionReglaNegocio("La fecha de regreso debe ser posterior a la fecha de salida");
        }

        SalidaViaje viaje = SalidaViaje.builder()
            .idUsuario(solicitud.idUsuario())
            .idPaquete(solicitud.idPaquete())
            .fechaSalida(solicitud.fechaSalida())
            .fechaRegreso(solicitud.fechaRegreso())
            .estado(ESTADO_VIAJE_PROGRAMADO)
            .build();

        SalidaViaje guardado = repositorioSalidaViaje.save(viaje);
        return mapearViaje(guardado, "Viaje programado correctamente");
    }

    public RespuestaTransporte asignarTransporte(Long idViaje, SolicitudTransporte solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        if (solicitud.capacidad() < solicitud.cantidadViajeros()) {
            throw new ExcepcionReglaNegocio("La capacidad del transporte no cubre la cantidad de viajeros");
        }

        // HU7.1: transport_assignment stores the assigned vehicle and validates capacity before saving.
        TransporteAsignado transporte = TransporteAsignado.builder()
            .idViaje(idViaje)
            .tipoTransporte(solicitud.tipoTransporte())
            .empresa(solicitud.empresa())
            .placa(solicitud.placa())
            .conductor(solicitud.conductor())
            .telefonoConductor(solicitud.telefonoConductor())
            .capacidad(solicitud.capacidad())
            .cantidadViajeros(solicitud.cantidadViajeros())
            .fechaSalida(solicitud.fechaSalida())
            .fechaRegistro(LocalDateTime.now())
            .build();

        TransporteAsignado guardado = repositorioTransporte.save(transporte);
        log.info("Transporte {} asignado al viaje {}", guardado.getId(), idViaje);
        return mapearTransporte(guardado, "Transporte asignado correctamente");
    }

    public RespuestaCheckIn registrarCheckIn(Long idViaje, SolicitudCheckIn solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        validarIdPositivo(solicitud.idViajero(), "idViajero");
        validarCodigoQr(idViaje, solicitud);

        if (repositorioCheckIn.existsByIdViajeAndIdViajero(idViaje, solicitud.idViajero())) {
            throw new ExcepcionReglaNegocio("El viajero ya tiene check-in registrado para este viaje");
        }

        // HU7.2: traveler_check_in prevents duplicate check-ins by trip and traveler.
        CheckInViajero checkIn = CheckInViajero.builder()
            .idViaje(idViaje)
            .idViajero(solicitud.idViajero())
            .codigoQr(solicitud.codigoQr())
            .idReserva(solicitud.idReserva())
            .fechaCheckIn(LocalDateTime.now())
            .build();

        CheckInViajero guardado = repositorioCheckIn.save(checkIn);
        log.info("Check-in {} registrado para viaje {} y viajero {}", guardado.getId(), idViaje, solicitud.idViajero());
        return mapearCheckIn(guardado, "Check-in registrado correctamente");
    }

    public RespuestaAlojamiento asignarAlojamiento(Long idViaje, SolicitudAlojamiento solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        validarIdPositivo(solicitud.idViajero(), "idViajero");
        if (!solicitud.fechaSalida().isAfter(solicitud.fechaIngreso())) {
            throw new ExcepcionReglaNegocio("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
        if (repositorioAlojamiento.existsByIdViajeAndIdViajero(idViaje, solicitud.idViajero())) {
            throw new ExcepcionReglaNegocio("El viajero ya tiene alojamiento asignado para este viaje");
        }
        if (repositorioAlojamiento.existsHabitacionOcupada(
            solicitud.hotel(),
            solicitud.habitacion(),
            solicitud.fechaIngreso(),
            solicitud.fechaSalida()
        )) {
            throw new ExcepcionReglaNegocio("La habitacion no esta disponible en las fechas solicitadas");
        }

        // HU7.3: lodging_assignment keeps hotel, room, address and stay dates for each traveler.
        AlojamientoAsignado alojamiento = AlojamientoAsignado.builder()
            .idViaje(idViaje)
            .idViajero(solicitud.idViajero())
            .hotel(solicitud.hotel())
            .habitacion(solicitud.habitacion())
            .direccion(solicitud.direccion())
            .fechaIngreso(solicitud.fechaIngreso())
            .fechaSalida(solicitud.fechaSalida())
            .fechaRegistro(LocalDateTime.now())
            .build();

        AlojamientoAsignado guardado = repositorioAlojamiento.save(alojamiento);
        return mapearAlojamiento(guardado, "Alojamiento asignado correctamente");
    }

    public RespuestaInformacionMedica registrarInformacionMedica(
        Long idViajero,
        SolicitudInformacionMedica solicitud
    ) {
        validarIdPositivo(idViajero, "idViajero");
        validarIdPositivo(solicitud.idViaje(), "idViaje");
        validarViajeExistente(solicitud.idViaje());
        validarTipoSangre(solicitud.tipoSangre());

        // HU7.4: traveler_medical_info centralizes medical data for operational response.
        InformacionMedica informacion = InformacionMedica.builder()
            .idViaje(solicitud.idViaje())
            .idViajero(idViajero)
            .tipoSangre(solicitud.tipoSangre().toUpperCase(Locale.ROOT))
            .alergias(solicitud.alergias())
            .medicamentos(solicitud.medicamentos())
            .condicionesMedicas(solicitud.condicionesMedicas())
            .telefonoMedico(solicitud.telefonoMedico())
            .fechaRegistro(LocalDateTime.now())
            .build();

        InformacionMedica guardada = repositorioInformacionMedica.save(informacion);
        return mapearInformacionMedica(guardada, "Informacion medica registrada correctamente");
    }

    public RespuestaContactoEmergencia registrarContactoEmergencia(
        Long idViajero,
        SolicitudContactoEmergencia solicitud
    ) {
        validarIdPositivo(idViajero, "idViajero");
        validarIdPositivo(solicitud.idViaje(), "idViaje");
        validarViajeExistente(solicitud.idViaje());

        // HU7.5: emergency_contact stores phone and email after request validation.
        ContactoEmergencia contacto = ContactoEmergencia.builder()
            .idViaje(solicitud.idViaje())
            .idViajero(idViajero)
            .nombre(solicitud.nombre())
            .parentesco(solicitud.parentesco())
            .telefono(solicitud.telefono())
            .correo(solicitud.correo())
            .fechaRegistro(LocalDateTime.now())
            .build();

        ContactoEmergencia guardado = repositorioContactoEmergencia.save(contacto);
        return mapearContacto(guardado, "Contacto de emergencia registrado correctamente");
    }

    public RespuestaIncidente registrarIncidente(Long idViaje, SolicitudIncidente solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        if (solicitud.idViajero() != null) {
            validarIdPositivo(solicitud.idViajero(), "idViajero");
        }

        // HU7.6: trip_incident starts in pending status and remains linked to the trip.
        IncidenteViaje incidente = IncidenteViaje.builder()
            .idViaje(idViaje)
            .idViajero(solicitud.idViajero())
            .tipo(solicitud.tipo())
            .descripcion(solicitud.descripcion())
            .severidad(solicitud.severidad().toUpperCase(Locale.ROOT))
            .estado(ESTADO_INCIDENTE_PENDIENTE)
            .reportadoPor(solicitud.reportadoPor())
            .fechaRegistro(LocalDateTime.now())
            .build();

        IncidenteViaje guardado = repositorioIncidente.save(incidente);
        return mapearIncidente(guardado, "Incidente registrado correctamente");
    }

    public RespuestaNotificacion enviarNotificacion(Long idViaje, SolicitudNotificacion solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        int totalDestinatarios = calcularTotalDestinatarios(idViaje, solicitud.destinatarios());
        if (totalDestinatarios == 0) {
            throw new ExcepcionReglaNegocio("No existen viajeros asociados al viaje para enviar la notificacion");
        }

        // HU7.7: notification_history records messages even when the real delivery provider is external.
        NotificacionViaje notificacion = NotificacionViaje.builder()
            .idViaje(idViaje)
            .asunto(solicitud.asunto())
            .mensaje(solicitud.mensaje())
            .canal(solicitud.canal().toUpperCase(Locale.ROOT))
            .totalDestinatarios(totalDestinatarios)
            .destinatarios(serializarDestinatarios(solicitud.destinatarios()))
            .fechaEnvio(LocalDateTime.now())
            .estado(ESTADO_ENVIADA)
            .build();

        NotificacionViaje guardada = repositorioNotificacion.save(notificacion);
        return mapearNotificacion(guardada, "Notificacion registrada para envio correctamente");
    }

    @Transactional(readOnly = true)
    public RespuestaDashboard obtenerDashboard(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        int viajerosRegistrados = calcularViajerosRegistrados(idViaje);
        int viajerosConCheckIn = Math.toIntExact(repositorioCheckIn.countByIdViaje(idViaje));
        double porcentajeCheckIn = viajerosRegistrados == 0
            ? 0.0
            : (viajerosConCheckIn * 100.0) / viajerosRegistrados;

        return new RespuestaDashboard(
            idViaje,
            viajerosRegistrados,
            viajerosConCheckIn,
            Math.toIntExact(repositorioTransporte.countByIdViaje(idViaje)),
            Math.toIntExact(repositorioAlojamiento.countByIdViaje(idViaje)),
            Math.toIntExact(repositorioIncidente.countByIdViaje(idViaje)),
            Math.toIntExact(repositorioIncidente.countByIdViajeAndEstado(idViaje, ESTADO_INCIDENTE_PENDIENTE)),
            Math.toIntExact(repositorioNotificacion.countByIdViaje(idViaje)),
            Math.round(porcentajeCheckIn * 100.0) / 100.0,
            LocalDateTime.now()
        );
    }

    private void validarCodigoQr(Long idViaje, SolicitudCheckIn solicitud) {
        // HU7.2: the QR is accepted only when it has a basic valid payload and the traveler can be linked.
        if (solicitud.codigoQr().trim().length() < 8) {
            throw new ExcepcionReglaNegocio("El codigo QR no tiene un formato valido");
        }
        if (solicitud.idReserva() == null && !existeViajeroAsociado(idViaje, solicitud.idViajero())) {
            throw new ExcepcionReglaNegocio("No se pudo verificar la reserva del viajero para este viaje");
        }
    }

    private boolean existeViajeroAsociado(Long idViaje, Long idViajero) {
        return repositorioAlojamiento.existsByIdViajeAndIdViajero(idViaje, idViajero)
            || repositorioInformacionMedica.existsByIdViajeAndIdViajero(idViaje, idViajero)
            || repositorioContactoEmergencia.existsByIdViajeAndIdViajero(idViaje, idViajero);
    }

    private int calcularTotalDestinatarios(Long idViaje, List<Long> destinatarios) {
        if (destinatarios != null && !destinatarios.isEmpty()) {
            return destinatarios.size();
        }
        return calcularViajerosRegistrados(idViaje);
    }

    private int calcularViajerosRegistrados(Long idViaje) {
        // HU7.8: dashboard metrics use the largest traveler evidence available in operation tables.
        long total = Math.max(repositorioCheckIn.countViajerosByIdViaje(idViaje), repositorioAlojamiento.countViajerosByIdViaje(idViaje));
        total = Math.max(total, repositorioInformacionMedica.countViajerosByIdViaje(idViaje));
        total = Math.max(total, repositorioContactoEmergencia.countViajerosByIdViaje(idViaje));
        return Math.toIntExact(total);
    }

    private String serializarDestinatarios(List<Long> destinatarios) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(",");
        destinatarios.forEach(destinatario -> joiner.add(String.valueOf(destinatario)));
        return joiner.toString();
    }

    private void validarTipoSangre(String tipoSangre) {
        String valor = tipoSangre.toUpperCase(Locale.ROOT);
        if (!valor.matches("^(A|B|AB|O)[+-]$")) {
            throw new ExcepcionReglaNegocio("El tipo de sangre no tiene un formato valido");
        }
    }

    private void validarIdPositivo(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(campo + " debe ser un valor positivo");
        }
    }

    private void validarViajeExistente(Long idViaje) {
        if (!repositorioSalidaViaje.existsById(idViaje)) {
            throw new ExcepcionReglaNegocio("El viaje no existe en trip_departure");
        }
    }

    private RespuestaViaje mapearViaje(SalidaViaje viaje, String mensaje) {
        return new RespuestaViaje(
            viaje.getId(),
            viaje.getIdUsuario(),
            viaje.getIdPaquete(),
            viaje.getFechaSalida(),
            viaje.getFechaRegreso(),
            viaje.getEstado(),
            mensaje
        );
    }

    private RespuestaTransporte mapearTransporte(TransporteAsignado transporte, String mensaje) {
        return new RespuestaTransporte(
            transporte.getId(),
            transporte.getIdViaje(),
            transporte.getTipoTransporte(),
            transporte.getEmpresa(),
            transporte.getPlaca(),
            transporte.getConductor(),
            transporte.getTelefonoConductor(),
            transporte.getCapacidad(),
            transporte.getCantidadViajeros(),
            transporte.getFechaSalida(),
            transporte.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaCheckIn mapearCheckIn(CheckInViajero checkIn, String mensaje) {
        return new RespuestaCheckIn(
            checkIn.getId(),
            checkIn.getIdViaje(),
            checkIn.getIdViajero(),
            checkIn.getCodigoQr(),
            checkIn.getIdReserva(),
            checkIn.getFechaCheckIn(),
            mensaje
        );
    }

    private RespuestaAlojamiento mapearAlojamiento(AlojamientoAsignado alojamiento, String mensaje) {
        return new RespuestaAlojamiento(
            alojamiento.getId(),
            alojamiento.getIdViaje(),
            alojamiento.getIdViajero(),
            alojamiento.getHotel(),
            alojamiento.getHabitacion(),
            alojamiento.getDireccion(),
            alojamiento.getFechaIngreso(),
            alojamiento.getFechaSalida(),
            alojamiento.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaInformacionMedica mapearInformacionMedica(InformacionMedica informacion, String mensaje) {
        return new RespuestaInformacionMedica(
            informacion.getId(),
            informacion.getIdViaje(),
            informacion.getIdViajero(),
            informacion.getTipoSangre(),
            informacion.getAlergias(),
            informacion.getMedicamentos(),
            informacion.getCondicionesMedicas(),
            informacion.getTelefonoMedico(),
            informacion.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaContactoEmergencia mapearContacto(ContactoEmergencia contacto, String mensaje) {
        return new RespuestaContactoEmergencia(
            contacto.getId(),
            contacto.getIdViaje(),
            contacto.getIdViajero(),
            contacto.getNombre(),
            contacto.getParentesco(),
            contacto.getTelefono(),
            contacto.getCorreo(),
            contacto.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaIncidente mapearIncidente(IncidenteViaje incidente, String mensaje) {
        return new RespuestaIncidente(
            incidente.getId(),
            incidente.getIdViaje(),
            incidente.getIdViajero(),
            incidente.getTipo(),
            incidente.getDescripcion(),
            incidente.getSeveridad(),
            incidente.getEstado(),
            incidente.getReportadoPor(),
            incidente.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaNotificacion mapearNotificacion(NotificacionViaje notificacion, String respuesta) {
        return new RespuestaNotificacion(
            notificacion.getId(),
            notificacion.getIdViaje(),
            notificacion.getAsunto(),
            notificacion.getMensaje(),
            notificacion.getCanal(),
            notificacion.getTotalDestinatarios(),
            notificacion.getFechaEnvio(),
            notificacion.getEstado(),
            respuesta
        );
    }
}
