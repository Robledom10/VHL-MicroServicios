package com.hernandolopera.operation_service.controladores;

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
import com.hernandolopera.operation_service.servicios.ServicioOperacion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operaciones")
@RequiredArgsConstructor
@Slf4j
public class ControladorOperacion {
    private final ServicioOperacion servicioOperacion;

    @PostMapping("/viajes")
    public ResponseEntity<RespuestaViaje> crearViaje(@Valid @RequestBody SolicitudViaje solicitud) {
        log.info("POST /api/v1/operaciones/viajes");
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.crearViaje(solicitud));
    }

    @PostMapping("/viajes/{idViaje}/transporte")
    public ResponseEntity<RespuestaTransporte> asignarTransporte(
        @PathVariable Long idViaje,
        @Valid @RequestBody SolicitudTransporte solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajes/{}/transporte", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.asignarTransporte(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/checkins")
    public ResponseEntity<RespuestaCheckIn> registrarCheckIn(
        @PathVariable Long idViaje,
        @Valid @RequestBody SolicitudCheckIn solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajes/{}/checkins", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.registrarCheckIn(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/alojamientos")
    public ResponseEntity<RespuestaAlojamiento> asignarAlojamiento(
        @PathVariable Long idViaje,
        @Valid @RequestBody SolicitudAlojamiento solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajes/{}/alojamientos", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.asignarAlojamiento(idViaje, solicitud));
    }

    @PostMapping("/viajeros/{idViajero}/informacion-medica")
    public ResponseEntity<RespuestaInformacionMedica> registrarInformacionMedica(
        @PathVariable Long idViajero,
        @Valid @RequestBody SolicitudInformacionMedica solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajeros/{}/informacion-medica", idViajero);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(servicioOperacion.registrarInformacionMedica(idViajero, solicitud));
    }

    @PostMapping("/viajeros/{idViajero}/contactos-emergencia")
    public ResponseEntity<RespuestaContactoEmergencia> registrarContactoEmergencia(
        @PathVariable Long idViajero,
        @Valid @RequestBody SolicitudContactoEmergencia solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajeros/{}/contactos-emergencia", idViajero);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(servicioOperacion.registrarContactoEmergencia(idViajero, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/incidentes")
    public ResponseEntity<RespuestaIncidente> registrarIncidente(
        @PathVariable Long idViaje,
        @Valid @RequestBody SolicitudIncidente solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajes/{}/incidentes", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.registrarIncidente(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/notificaciones")
    public ResponseEntity<RespuestaNotificacion> enviarNotificacion(
        @PathVariable Long idViaje,
        @Valid @RequestBody SolicitudNotificacion solicitud
    ) {
        log.info("POST /api/v1/operaciones/viajes/{}/notificaciones", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.enviarNotificacion(idViaje, solicitud));
    }

    @GetMapping("/viajes/{idViaje}/dashboard")
    public ResponseEntity<RespuestaDashboard> obtenerDashboard(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/dashboard", idViaje);
        return ResponseEntity.ok(servicioOperacion.obtenerDashboard(idViaje));
    }
}
