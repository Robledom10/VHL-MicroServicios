package com.hernandolopera.operation_service.controladores;

import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaAlojamiento;

import com.hernandolopera.operation_service.dto.DatosOperacion.RespuestaRestaurante;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudRestaurante;
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
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudCheckIn;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudContactoEmergencia;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudIncidente;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudInformacionMedica;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudNotificacion;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudTransporte;
import com.hernandolopera.operation_service.dto.DatosOperacion.SolicitudViaje;
import com.hernandolopera.operation_service.servicios.ServicioOperacion;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operaciones")
@RequiredArgsConstructor
@Slf4j
public class ControladorOperacion {
    private final ServicioOperacion servicioOperacion;

    // =========================================================
    // POST — crear
    // =========================================================

    @PostMapping("/viajes")
    public ResponseEntity<RespuestaViaje> crearViaje(@Valid @RequestBody SolicitudViaje solicitud) {
        log.info("POST /api/v1/operaciones/viajes");
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.crearViaje(solicitud));
    }

    @PostMapping("/viajes/{idViaje}/transporte")
    public ResponseEntity<RespuestaTransporte> asignarTransporte(
            @PathVariable Long idViaje,
            @Valid @RequestBody SolicitudTransporte solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/transporte", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.asignarTransporte(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/checkins")
    public ResponseEntity<RespuestaCheckIn> registrarCheckIn(
            @PathVariable Long idViaje,
            @Valid @RequestBody SolicitudCheckIn solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/checkins", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.registrarCheckIn(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/alojamientos")
    public ResponseEntity<RespuestaAlojamiento> asignarAlojamiento(
            @PathVariable Long idViaje,
            @Valid @RequestBody SolicitudAlojamiento solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/alojamientos", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.asignarAlojamiento(idViaje, solicitud));
    }

    @PostMapping("/viajeros/{idViajero}/informacion-medica")
    public ResponseEntity<RespuestaInformacionMedica> registrarInformacionMedica(
            @PathVariable Long idViajero,
            @Valid @RequestBody SolicitudInformacionMedica solicitud) {
        log.info("POST /api/v1/operaciones/viajeros/{}/informacion-medica", idViajero);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioOperacion.registrarInformacionMedica(idViajero, solicitud));
    }

    @PostMapping("/viajeros/{idViajero}/contactos-emergencia")
    public ResponseEntity<RespuestaContactoEmergencia> registrarContactoEmergencia(
            @PathVariable Long idViajero,
            @Valid @RequestBody SolicitudContactoEmergencia solicitud) {
        log.info("POST /api/v1/operaciones/viajeros/{}/contactos-emergencia", idViajero);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioOperacion.registrarContactoEmergencia(idViajero, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/incidentes")
    public ResponseEntity<RespuestaIncidente> registrarIncidente(
            @PathVariable Long idViaje,
            @Valid @RequestBody SolicitudIncidente solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/incidentes", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.registrarIncidente(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/restaurantes")
    public ResponseEntity<RespuestaRestaurante> asignarRestaurante(@PathVariable Long idViaje,
            @Valid @RequestBody SolicitudRestaurante solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/restaurantes", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.asignarRestaurante(idViaje, solicitud));
    }

    @PostMapping("/viajes/{idViaje}/notificaciones")
    public ResponseEntity<RespuestaNotificacion> enviarNotificacion(
            @PathVariable Long idViaje,
            @Valid @RequestBody SolicitudNotificacion solicitud) {
        log.info("POST /api/v1/operaciones/viajes/{}/notificaciones", idViaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioOperacion.enviarNotificacion(idViaje, solicitud));
    }

    // =========================================================
    // GET — listar
    // =========================================================

    @GetMapping("/viajes")
    public ResponseEntity<List<RespuestaViaje>> listarViajes() {
        log.info("GET /api/v1/operaciones/viajes");
        return ResponseEntity.ok(servicioOperacion.listarViajes());
    }

    @GetMapping("/viajes/{idViaje}/transporte")
    public ResponseEntity<List<RespuestaTransporte>> listarTransportes(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/transporte", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarTransportes(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/checkins")
    public ResponseEntity<List<RespuestaCheckIn>> listarCheckIns(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/checkins", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarCheckIns(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/alojamientos")
    public ResponseEntity<List<RespuestaAlojamiento>> listarAlojamientos(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/alojamientos", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarAlojamientos(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/restaurantes")
    public ResponseEntity<List<RespuestaRestaurante>> listarRestaurantes(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/restaurantes", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarRestaurantes(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/informacion-medica")
    public ResponseEntity<List<RespuestaInformacionMedica>> listarInformacionMedica(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/informacion-medica", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarInformacionMedica(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/contactos-emergencia")
    public ResponseEntity<List<RespuestaContactoEmergencia>> listarContactos(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/contactos-emergencia", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarContactos(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/incidentes")
    public ResponseEntity<List<RespuestaIncidente>> listarIncidentes(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/incidentes", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarIncidentes(idViaje));
    }

    @GetMapping("/viajes/{idViaje}/notificaciones")
    public ResponseEntity<List<RespuestaNotificacion>> listarNotificaciones(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/notificaciones", idViaje);
        return ResponseEntity.ok(servicioOperacion.listarNotificaciones(idViaje));
    }

    @PutMapping("/viajes/{idViaje}/notificaciones/{id}")
    public ResponseEntity<RespuestaNotificacion> actualizarNotificacion(
            @PathVariable Long idViaje, @PathVariable Long id,
            @Valid @RequestBody SolicitudNotificacion solicitud) {
        log.info("PUT /api/v1/operaciones/viajes/{}/notificaciones/{}", idViaje, id);
        return ResponseEntity.ok(servicioOperacion.actualizarNotificacion(idViaje, id, solicitud));
    }

    @DeleteMapping("/viajes/{idViaje}/notificaciones/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long idViaje, @PathVariable Long id) {
        log.info("DELETE /api/v1/operaciones/viajes/{}/notificaciones/{}", idViaje, id);
        servicioOperacion.eliminarNotificacion(idViaje, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/viajes/{idViaje}/dashboard")
    public ResponseEntity<RespuestaDashboard> obtenerDashboard(@PathVariable Long idViaje) {
        log.info("GET /api/v1/operaciones/viajes/{}/dashboard", idViaje);
        return ResponseEntity.ok(servicioOperacion.obtenerDashboard(idViaje));
    }

    // =========================================================
    // PUT / PATCH — actualizar
    // =========================================================

    @PutMapping("/viajes/{id}")
    public ResponseEntity<RespuestaViaje> actualizarViaje(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudViaje solicitud) {
        log.info("PUT /api/v1/operaciones/viajes/{}", id);
        return ResponseEntity.ok(servicioOperacion.actualizarViaje(id, solicitud));
    }

    @DeleteMapping("/viajes/{id}")
    public ResponseEntity<Void> eliminarViaje(@PathVariable Long id) {
        log.info("DELETE /api/v1/operaciones/viajes/{}", id);
        servicioOperacion.eliminarViaje(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/viajes/{idViaje}/alojamientos/{id}")
    public ResponseEntity<RespuestaAlojamiento> actualizarAlojamiento(
            @PathVariable Long idViaje,
            @PathVariable Long id,
            @Valid @RequestBody SolicitudAlojamiento solicitud) {
        log.info("PUT /api/v1/operaciones/viajes/{}/alojamientos/{}", idViaje, id);
        return ResponseEntity.ok(servicioOperacion.actualizarAlojamiento(idViaje, id, solicitud));
    }

    @PutMapping("/viajes/{idViaje}/transporte/{id}")
    public ResponseEntity<RespuestaTransporte> actualizarTransporte(
            @PathVariable Long idViaje,
            @PathVariable Long id,
            @Valid @RequestBody SolicitudTransporte solicitud) {
        log.info("PUT /api/v1/operaciones/viajes/{}/transporte/{}", idViaje, id);
        return ResponseEntity.ok(servicioOperacion.actualizarTransporte(idViaje, id, solicitud));
    }

    @PutMapping("/viajes/{idViaje}/restaurantes/{id}")
    public ResponseEntity<RespuestaRestaurante> actualizarRestaurante(@PathVariable Long idViaje, @PathVariable Long id,
            @Valid @RequestBody SolicitudRestaurante solicitud) {
        log.info("PUT /api/v1/operaciones/viajes/{}/restaurantes/{}", idViaje, id);
        return ResponseEntity.ok(servicioOperacion.actualizarRestaurante(idViaje, id, solicitud));
    }

    @PutMapping("/incidentes/{id}")
    public ResponseEntity<RespuestaIncidente> actualizarIncidente(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudIncidente solicitud) {
        log.info("PUT /api/v1/operaciones/incidentes/{}", id);
        return ResponseEntity.ok(servicioOperacion.actualizarIncidente(id, solicitud));
    }

    @PatchMapping("/incidentes/{id}/estado")
    public ResponseEntity<RespuestaIncidente> actualizarEstadoIncidente(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudActualizarEstadoIncidente solicitud) {
        log.info("PATCH /api/v1/operaciones/incidentes/{}/estado", id);
        return ResponseEntity.ok(servicioOperacion.actualizarEstadoIncidente(id, solicitud));
    }

    @PutMapping("/viajeros/{idViajero}/informacion-medica/{id}")
    public ResponseEntity<RespuestaInformacionMedica> actualizarInformacionMedica(
            @PathVariable Long idViajero,
            @PathVariable Long id,
            @Valid @RequestBody SolicitudInformacionMedica solicitud) {
        log.info("PUT /api/v1/operaciones/viajeros/{}/informacion-medica/{}", idViajero, id);
        return ResponseEntity.ok(servicioOperacion.actualizarInformacionMedica(idViajero, id, solicitud));
    }

    @DeleteMapping("/viajeros/{idViajero}/informacion-medica/{id}")
    public ResponseEntity<Void> eliminarInformacionMedica(
            @PathVariable Long idViajero,
            @PathVariable Long id) {
        log.info("DELETE /api/v1/operaciones/viajeros/{}/informacion-medica/{}", idViajero, id);
        servicioOperacion.eliminarInformacionMedica(idViajero, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/viajeros/{idViajero}/contactos-emergencia/{id}")
    public ResponseEntity<RespuestaContactoEmergencia> actualizarContacto(
            @PathVariable Long idViajero,
            @PathVariable Long id,
            @Valid @RequestBody SolicitudContactoEmergencia solicitud) {
        log.info("PUT /api/v1/operaciones/viajeros/{}/contactos-emergencia/{}", idViajero, id);
        return ResponseEntity.ok(servicioOperacion.actualizarContacto(idViajero, id, solicitud));
    }

    @DeleteMapping("/viajes/{idViaje}/restaurantes/{id}")
    public ResponseEntity<Void> eliminarRestaurante(@PathVariable Long idViaje, @PathVariable Long id) {
        log.info("DELETE /api/v1/operaciones/viajes/{}/restaurantes/{}", idViaje, id);
        servicioOperacion.eliminarRestaurante(idViaje, id);
        return ResponseEntity.noContent().build();
    }
}
