package com.hernandolopera.operation_service.servicios;

import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaAlojamiento;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaGuia;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaRestaurante;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaCheckIn;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaContactoEmergencia;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaDashboard;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaInformacionMedica;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaNotificacion;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaTransporte;
import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaViaje;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudActualizarEstadoIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudAlojamiento;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudGuia;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudRestaurante;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudCheckIn;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudContactoEmergencia;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudInformacionMedica;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudNotificacion;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudTransporte;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudViaje;
import com.hernandolopera.operation_service.entidades.AlojamientoAsignado;
import com.hernandolopera.operation_service.entidades.GuiaAsignado;
import com.hernandolopera.operation_service.entidades.RestauranteViaje;
import com.hernandolopera.operation_service.entidades.CheckInViajero;
import com.hernandolopera.operation_service.entidades.ContactoEmergencia;
import com.hernandolopera.operation_service.entidades.IncidenteViaje;
import com.hernandolopera.operation_service.entidades.InformacionMedica;
import com.hernandolopera.operation_service.entidades.NotificacionViaje;
import com.hernandolopera.operation_service.entidades.SalidaViaje;
import com.hernandolopera.operation_service.entidades.TransporteAsignado;
import com.hernandolopera.operation_service.excepciones.ExcepcionReglaNegocio;
import com.hernandolopera.operation_service.excepciones.RecursoNoEncontradoExcepcion;
import com.hernandolopera.operation_service.repositorios.RepositorioAlojamientoAsignado;
import com.hernandolopera.operation_service.repositorios.RepositorioGuiaAsignado;
import com.hernandolopera.operation_service.repositorios.RepositorioRestauranteViaje;
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
    private final RepositorioGuiaAsignado repositorioGuia;
    private final RepositorioRestauranteViaje repositorioRestaurante;
    private final ServicioEmail servicioEmail;
    private final ServicioWhatsApp servicioWhatsApp;

    // =========================================================
    // CREATE
    // =========================================================

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

        AlojamientoAsignado alojamiento = AlojamientoAsignado.builder()
            .idViaje(idViaje)
            .idViajero(solicitud.idViajero())
            .nombreViajero(solicitud.nombreViajero())
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

    public RespuestaGuia asignarGuia(Long idViaje, SolicitudGuia solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        GuiaAsignado guia = GuiaAsignado.builder()
            .idViaje(idViaje)
            .nombreGuia(solicitud.nombreGuia())
            .telefono(solicitud.telefono())
            .correo(solicitud.correo())
            .especialidad(solicitud.especialidad())
            .idioma(solicitud.idioma())
            .fechaRegistro(LocalDateTime.now())
            .build();
        return mapearGuia(repositorioGuia.save(guia), "Guía asignado correctamente");
    }

    public RespuestaGuia actualizarGuia(Long idViaje, Long idGuia, SolicitudGuia solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        GuiaAsignado guia = repositorioGuia.findById(idGuia)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Guía no encontrado"));
        guia.setNombreGuia(solicitud.nombreGuia());
        guia.setTelefono(solicitud.telefono());
        guia.setCorreo(solicitud.correo());
        guia.setEspecialidad(solicitud.especialidad());
        guia.setIdioma(solicitud.idioma());
        return mapearGuia(repositorioGuia.save(guia), "Guía actualizado correctamente");
    }

    @Transactional(readOnly = true)
    public List<RespuestaGuia> listarGuias(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioGuia.findAllByIdViaje(idViaje).stream()
            .map(g -> mapearGuia(g, null)).toList();
    }

    public void eliminarGuia(Long idViaje, Long idGuia) {
        validarIdPositivo(idViaje, "idViaje");
        if (!repositorioGuia.existsById(idGuia))
            throw new RecursoNoEncontradoExcepcion("Guía no encontrado");
        repositorioGuia.deleteById(idGuia);
    }

    public RespuestaRestaurante asignarRestaurante(Long idViaje, SolicitudRestaurante solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarViajeExistente(idViaje);
        RestauranteViaje restaurante = RestauranteViaje.builder()
            .idViaje(idViaje)
            .nombreRestaurante(solicitud.nombreRestaurante())
            .direccion(solicitud.direccion())
            .telefono(solicitud.telefono())
            .tipoComida(solicitud.tipoComida())
            .notas(solicitud.notas())
            .fechaRegistro(LocalDateTime.now())
            .build();
        return mapearRestaurante(repositorioRestaurante.save(restaurante), "Restaurante asignado correctamente");
    }

    public RespuestaRestaurante actualizarRestaurante(Long idViaje, Long idRestaurante, SolicitudRestaurante solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        RestauranteViaje r = repositorioRestaurante.findById(idRestaurante)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Restaurante no encontrado"));
        r.setNombreRestaurante(solicitud.nombreRestaurante());
        r.setDireccion(solicitud.direccion());
        r.setTelefono(solicitud.telefono());
        r.setTipoComida(solicitud.tipoComida());
        r.setNotas(solicitud.notas());
        return mapearRestaurante(repositorioRestaurante.save(r), "Restaurante actualizado correctamente");
    }

    @Transactional(readOnly = true)
    public List<RespuestaRestaurante> listarRestaurantes(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioRestaurante.findAllByIdViaje(idViaje).stream()
            .map(r -> mapearRestaurante(r, null)).toList();
    }

    public void eliminarRestaurante(Long idViaje, Long idRestaurante) {
        validarIdPositivo(idViaje, "idViaje");
        if (!repositorioRestaurante.existsById(idRestaurante))
            throw new RecursoNoEncontradoExcepcion("Restaurante no encontrado");
        repositorioRestaurante.deleteById(idRestaurante);
    }

    public RespuestaInformacionMedica registrarInformacionMedica(
        Long idViajero,
        SolicitudInformacionMedica solicitud
    ) {
        validarIdPositivo(idViajero, "idViajero");
        validarIdPositivo(solicitud.idViaje(), "idViaje");
        validarViajeExistente(solicitud.idViaje());
        validarTipoSangre(solicitud.tipoSangre());

        InformacionMedica informacion = InformacionMedica.builder()
            .idViaje(solicitud.idViaje())
            .idViajero(idViajero)
            .tipoSangre(solicitud.tipoSangre().toUpperCase(Locale.ROOT))
            .alergias(solicitud.alergias())
            .medicamentos(solicitud.medicamentos())
            .condicionesMedicas(solicitud.condicionesMedicas())
            .telefonoMedico(solicitud.telefonoMedico())
            .nombreViajero(solicitud.nombreViajero())
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

        ContactoEmergencia contacto = ContactoEmergencia.builder()
            .idViaje(solicitud.idViaje())
            .idViajero(idViajero)
            .nombre(solicitud.nombre())
            .parentesco(solicitud.parentesco())
            .telefono(solicitud.telefono())
            .correo(solicitud.correo())
            .nombreViajero(solicitud.nombreViajero())
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

        List<String> contactos = solicitud.contactos() != null ? solicitud.contactos() : List.of();
        int totalDestinatarios = contactos.isEmpty()
            ? calcularTotalDestinatarios(idViaje, solicitud.destinatarios())
            : contactos.size();

        if (totalDestinatarios == 0) {
            throw new ExcepcionReglaNegocio("No existen viajeros asociados al viaje para enviar la notificacion");
        }

        String canalUpper = solicitud.canal().toUpperCase(Locale.ROOT);
        NotificacionViaje notificacion = NotificacionViaje.builder()
            .idViaje(idViaje)
            .asunto(solicitud.asunto())
            .mensaje(solicitud.mensaje())
            .canal(canalUpper)
            .totalDestinatarios(totalDestinatarios)
            .destinatarios(serializarDestinatarios(solicitud.destinatarios()))
            .fechaEnvio(LocalDateTime.now())
            .estado(ESTADO_ENVIADA)
            .build();

        NotificacionViaje guardada = repositorioNotificacion.save(notificacion);

        if (!contactos.isEmpty()) {
            if ("EMAIL".equals(canalUpper)) {
                servicioEmail.enviarMasivo(contactos, solicitud.asunto(), solicitud.mensaje());
            } else if ("WHATSAPP".equals(canalUpper)) {
                servicioWhatsApp.enviarMasivo(contactos, solicitud.mensaje());
            }
        }

        return mapearNotificacion(guardada, "Notificacion registrada para envio correctamente");
    }

    // =========================================================
    // READ (LIST)
    // =========================================================

    @Transactional(readOnly = true)
    public List<RespuestaViaje> listarViajes() {
        return repositorioSalidaViaje.findAll()
            .stream()
            .map(v -> mapearViaje(v, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaTransporte> listarTransportes(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioTransporte.findAllByIdViaje(idViaje)
            .stream()
            .map(t -> mapearTransporte(t, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaCheckIn> listarCheckIns(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioCheckIn.findAllByIdViaje(idViaje)
            .stream()
            .map(c -> mapearCheckIn(c, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaAlojamiento> listarAlojamientos(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioAlojamiento.findAllByIdViaje(idViaje)
            .stream()
            .map(a -> mapearAlojamiento(a, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaInformacionMedica> listarInformacionMedica(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioInformacionMedica.findAllByIdViaje(idViaje)
            .stream()
            .map(i -> mapearInformacionMedica(i, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaContactoEmergencia> listarContactos(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioContactoEmergencia.findAllByIdViaje(idViaje)
            .stream()
            .map(c -> mapearContacto(c, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaIncidente> listarIncidentes(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioIncidente.findAllByIdViaje(idViaje)
            .stream()
            .map(i -> mapearIncidente(i, null))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaNotificacion> listarNotificaciones(Long idViaje) {
        validarIdPositivo(idViaje, "idViaje");
        return repositorioNotificacion.findAllByIdViaje(idViaje)
            .stream()
            .map(n -> mapearNotificacion(n, null))
            .toList();
    }

    @Transactional
    public RespuestaNotificacion actualizarNotificacion(Long idViaje, Long id, SolicitudNotificacion solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        NotificacionViaje notificacion = repositorioNotificacion.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No existe la notificacion con id " + id));
        notificacion.setAsunto(solicitud.asunto());
        notificacion.setMensaje(solicitud.mensaje());
        return mapearNotificacion(repositorioNotificacion.save(notificacion), "Notificacion actualizada");
    }

    @Transactional
    public void eliminarNotificacion(Long idViaje, Long id) {
        validarIdPositivo(idViaje, "idViaje");
        if (!repositorioNotificacion.existsById(id))
            throw new RecursoNoEncontradoExcepcion("No existe la notificacion con id " + id);
        repositorioNotificacion.deleteById(id);
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

    // =========================================================
    // UPDATE
    // =========================================================

    public RespuestaViaje actualizarViaje(Long id, SolicitudViaje solicitud) {
        validarIdPositivo(id, "id");
        if (!solicitud.fechaRegreso().isAfter(solicitud.fechaSalida())) {
            throw new ExcepcionReglaNegocio("La fecha de regreso debe ser posterior a la fecha de salida");
        }
        SalidaViaje viaje = repositorioSalidaViaje.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Viaje no encontrado con id: " + id));
        viaje.setFechaSalida(solicitud.fechaSalida());
        viaje.setFechaRegreso(solicitud.fechaRegreso());
        SalidaViaje guardado = repositorioSalidaViaje.save(viaje);
        log.info("Viaje {} actualizado", guardado.getId());
        return mapearViaje(guardado, "Viaje actualizado correctamente");
    }

    public void eliminarViaje(Long id) {
        validarIdPositivo(id, "id");
        if (!repositorioSalidaViaje.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Viaje no encontrado con id: " + id);
        }
        repositorioTransporte.deleteAllByIdViaje(id);
        repositorioCheckIn.deleteAllByIdViaje(id);
        repositorioAlojamiento.deleteAllByIdViaje(id);
        repositorioInformacionMedica.deleteAllByIdViaje(id);
        repositorioContactoEmergencia.deleteAllByIdViaje(id);
        repositorioIncidente.deleteAllByIdViaje(id);
        repositorioNotificacion.deleteAllByIdViaje(id);
        repositorioSalidaViaje.deleteById(id);
        log.info("Viaje {} eliminado con todos sus datos asociados", id);
    }

    public void eliminarInformacionMedica(Long idViajero, Long id) {
        validarIdPositivo(id, "id");
        if (!repositorioInformacionMedica.existsById(id)) {
            throw new RecursoNoEncontradoExcepcion("Informacion medica no encontrada con id: " + id);
        }
        repositorioInformacionMedica.deleteById(id);
        log.info("Informacion medica {} eliminada", id);
    }

    public RespuestaAlojamiento actualizarAlojamiento(Long idViaje, Long id, SolicitudAlojamiento solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarIdPositivo(id, "id");
        if (!solicitud.fechaSalida().isAfter(solicitud.fechaIngreso())) {
            throw new ExcepcionReglaNegocio("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
        AlojamientoAsignado alojamiento = repositorioAlojamiento.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Alojamiento no encontrado con id: " + id));
        alojamiento.setIdViajero(solicitud.idViajero());
        alojamiento.setNombreViajero(solicitud.nombreViajero());
        alojamiento.setHotel(solicitud.hotel());
        alojamiento.setHabitacion(solicitud.habitacion());
        alojamiento.setDireccion(solicitud.direccion());
        alojamiento.setFechaIngreso(solicitud.fechaIngreso());
        alojamiento.setFechaSalida(solicitud.fechaSalida());
        AlojamientoAsignado guardado = repositorioAlojamiento.save(alojamiento);
        log.info("Alojamiento {} actualizado para el viaje {}", guardado.getId(), idViaje);
        return mapearAlojamiento(guardado, "Alojamiento actualizado correctamente");
    }

    public RespuestaTransporte actualizarTransporte(Long idViaje, Long id, SolicitudTransporte solicitud) {
        validarIdPositivo(idViaje, "idViaje");
        validarIdPositivo(id, "id");
        if (solicitud.capacidad() < solicitud.cantidadViajeros()) {
            throw new ExcepcionReglaNegocio("La capacidad del transporte no cubre la cantidad de viajeros");
        }
        TransporteAsignado transporte = repositorioTransporte.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Transporte no encontrado con id: " + id));
        transporte.setTipoTransporte(solicitud.tipoTransporte());
        transporte.setEmpresa(solicitud.empresa());
        transporte.setPlaca(solicitud.placa());
        transporte.setConductor(solicitud.conductor());
        transporte.setTelefonoConductor(solicitud.telefonoConductor());
        transporte.setCapacidad(solicitud.capacidad());
        transporte.setCantidadViajeros(solicitud.cantidadViajeros());
        transporte.setFechaSalida(solicitud.fechaSalida());
        TransporteAsignado guardado = repositorioTransporte.save(transporte);
        log.info("Transporte {} actualizado para el viaje {}", guardado.getId(), idViaje);
        return mapearTransporte(guardado, "Transporte actualizado correctamente");
    }

    public RespuestaIncidente actualizarIncidente(Long id, SolicitudIncidente solicitud) {
        validarIdPositivo(id, "id");
        IncidenteViaje incidente = repositorioIncidente.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Incidente no encontrado con id: " + id));
        incidente.setTipo(solicitud.tipo());
        incidente.setDescripcion(solicitud.descripcion());
        incidente.setSeveridad(solicitud.severidad());
        incidente.setReportadoPor(solicitud.reportadoPor());
        incidente.setIdViajero(solicitud.idViajero());
        IncidenteViaje guardado = repositorioIncidente.save(incidente);
        log.info("Incidente {} actualizado", guardado.getId());
        return mapearIncidente(guardado, "Incidente actualizado correctamente");
    }

    public RespuestaIncidente actualizarEstadoIncidente(Long id, SolicitudActualizarEstadoIncidente solicitud) {
        IncidenteViaje incidente = repositorioIncidente.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Incidente no encontrado con id: " + id));
        incidente.setEstado(solicitud.estado());
        IncidenteViaje guardado = repositorioIncidente.save(incidente);
        return mapearIncidente(guardado, "Estado del incidente actualizado correctamente");
    }

    public RespuestaInformacionMedica actualizarInformacionMedica(
        Long idViajero, Long id, SolicitudInformacionMedica solicitud
    ) {
        InformacionMedica info = repositorioInformacionMedica.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Informacion medica no encontrada con id: " + id));
        validarTipoSangre(solicitud.tipoSangre());
        info.setTipoSangre(solicitud.tipoSangre().toUpperCase(Locale.ROOT));
        info.setAlergias(solicitud.alergias());
        info.setMedicamentos(solicitud.medicamentos());
        info.setCondicionesMedicas(solicitud.condicionesMedicas());
        info.setTelefonoMedico(solicitud.telefonoMedico());
        info.setNombreViajero(solicitud.nombreViajero());
        InformacionMedica guardada = repositorioInformacionMedica.save(info);
        return mapearInformacionMedica(guardada, "Informacion medica actualizada correctamente");
    }

    public RespuestaContactoEmergencia actualizarContacto(
        Long idViajero, Long id, SolicitudContactoEmergencia solicitud
    ) {
        ContactoEmergencia contacto = repositorioContactoEmergencia.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Contacto de emergencia no encontrado con id: " + id));
        contacto.setNombre(solicitud.nombre());
        contacto.setParentesco(solicitud.parentesco());
        contacto.setTelefono(solicitud.telefono());
        contacto.setCorreo(solicitud.correo());
        contacto.setNombreViajero(solicitud.nombreViajero());
        ContactoEmergencia guardado = repositorioContactoEmergencia.save(contacto);
        return mapearContacto(guardado, "Contacto de emergencia actualizado correctamente");
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private void validarCodigoQr(Long idViaje, SolicitudCheckIn solicitud) {
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
            alojamiento.getNombreViajero(),
            alojamiento.getHotel(),
            alojamiento.getHabitacion(),
            alojamiento.getDireccion(),
            alojamiento.getFechaIngreso(),
            alojamiento.getFechaSalida(),
            alojamiento.getFechaRegistro(),
            mensaje
        );
    }

    private RespuestaGuia mapearGuia(GuiaAsignado g, String mensaje) {
        return new RespuestaGuia(g.getId(), g.getIdViaje(), g.getNombreGuia(),
            g.getTelefono(), g.getCorreo(), g.getEspecialidad(), g.getIdioma(),
            g.getFechaRegistro(), mensaje);
    }

    private RespuestaRestaurante mapearRestaurante(RestauranteViaje r, String mensaje) {
        return new RespuestaRestaurante(r.getId(), r.getIdViaje(), r.getNombreRestaurante(),
            r.getDireccion(), r.getTelefono(), r.getTipoComida(), r.getNotas(),
            r.getFechaRegistro(), mensaje);
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
            mensaje,
            informacion.getNombreViajero()
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
            mensaje,
            contacto.getNombreViajero()
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
