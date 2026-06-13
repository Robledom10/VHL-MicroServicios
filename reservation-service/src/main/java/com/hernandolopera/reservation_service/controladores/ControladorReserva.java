package com.hernandolopera.reservation_service.controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.reservation_service.dto.ReservaDTO;
import com.hernandolopera.reservation_service.dto.SolicitudCrearReserva;
import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;
import com.hernandolopera.reservation_service.servicios.ServicioReserva;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
public class ControladorReserva {

	private final ServicioReserva servicioReserva;

	/**
	 * Obtiene todas las reservas (panel admin)
	 */
	@GetMapping
	public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas() {
		log.info("GET /api/v1/reservas - Obteniendo todas las reservas");
		return ResponseEntity.ok(servicioReserva.obtenerTodasLasReservas());
	}

	/**
	 * SCRUM-704: Crear servicio para obtener reservas
	 * Obtiene una reserva por su ID
	 */
	@GetMapping("/{idReserva}")
	public ResponseEntity<ReservaDTO> obtenerReservaPorId(@PathVariable("idReserva") Long idReserva) {
		log.info("GET /api/v1/reservas/{} - Obteniendo reserva por ID", idReserva);
		Optional<ReservaDTO> reserva = servicioReserva.obtenerReservaPorId(idReserva);

		if (reserva.isPresent()) {
			return ResponseEntity.ok(reserva.get());
		} else {
			log.warn("Reserva no encontrada con ID: {}", idReserva);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Obtiene todas las reservas de un paquete turístico
	 */
	@GetMapping("/paquete/{idPaquete}")
	public ResponseEntity<List<ReservaDTO>> obtenerReservasPorPaquete(@PathVariable("idPaquete") Long idPaquete) {
		log.info("GET /api/v1/reservas/paquete/{} - Obteniendo reservas por paquete", idPaquete);
		return ResponseEntity.ok(servicioReserva.obtenerReservasPorPaquete(idPaquete));
	}

	/**
	 * SCRUM-704: Crear servicio para obtener reservas
	 * Obtiene una reserva por su número
	 */
	@GetMapping("/numero/{numeroReserva}")
	public ResponseEntity<ReservaDTO> obtenerReservaPorNumero(@PathVariable("numeroReserva") String numeroReserva) {
		log.info("GET /api/v1/reservas/numero/{} - Obteniendo reserva por número", numeroReserva);
		Optional<ReservaDTO> reserva = servicioReserva.obtenerReservaPorNumero(numeroReserva);

		if (reserva.isPresent()) {
			return ResponseEntity.ok(reserva.get());
		} else {
			log.warn("Reserva no encontrada con número: {}", numeroReserva);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Obtiene todas las reservas de un usuario
	 */
	@GetMapping("/usuario/{idUsuario}")
	public ResponseEntity<List<ReservaDTO>> obtenerReservasDelUsuario(@PathVariable("idUsuario") Long idUsuario) {
		log.info("GET /api/v1/reservas/usuario/{} - Obteniendo reservas del usuario", idUsuario);
		List<ReservaDTO> reservas = servicioReserva.obtenerReservasDelUsuario(idUsuario);
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Obtiene las reservas confirmadas de un usuario
	 */
	@GetMapping("/usuario/{idUsuario}/confirmadas")
	public ResponseEntity<List<ReservaDTO>> obtenerReservasConfirmadasDelUsuario(@PathVariable("idUsuario") Long idUsuario) {
		log.info("GET /api/v1/reservas/usuario/{}/confirmadas - Obteniendo reservas confirmadas", idUsuario);
		List<ReservaDTO> reservas = servicioReserva.obtenerReservasConfirmadasDelUsuario(idUsuario);
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Obtiene las reservas pasadas de un usuario
	 */
	@GetMapping("/usuario/{idUsuario}/pasadas")
	public ResponseEntity<List<ReservaDTO>> obtenerReservasPasadasDelUsuario(@PathVariable("idUsuario") Long idUsuario) {
		log.info("GET /api/v1/reservas/usuario/{}/pasadas - Obteniendo reservas pasadas", idUsuario);
		List<ReservaDTO> reservas = servicioReserva.obtenerReservasPasadasDelUsuario(idUsuario);
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Obtiene reservas por estado
	 */
	@GetMapping("/estado/{estado}")
	public ResponseEntity<List<ReservaDTO>> obtenerReservasPorEstado(@PathVariable("estado") EstadoReserva estado) {
		log.info("GET /api/v1/reservas/estado/{} - Obteniendo reservas por estado", estado);
		List<ReservaDTO> reservas = servicioReserva.obtenerReservasPorEstado(estado);
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Crea una nueva reserva desde el formulario del frontend
	 */
	@PostMapping
	public ResponseEntity<ReservaDTO> crearReserva(@Valid @RequestBody SolicitudCrearReserva solicitud) {
		log.info("POST /api/v1/reservas - Creando nueva reserva para: {}", solicitud.getClienteNombre());
		ReservaDTO reservaCreada = servicioReserva.crearReserva(solicitud);
		return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
	}

	/**
	 * Confirma una reserva directamente (panel admin)
	 */
	@PutMapping("/{idReserva}/confirmar")
	public ResponseEntity<ReservaDTO> confirmarReserva(@PathVariable("idReserva") Long idReserva) {
		log.info("PUT /api/v1/reservas/{}/confirmar - Confirmando reserva", idReserva);
		try {
			return ResponseEntity.ok(servicioReserva.confirmarReserva(idReserva));
		} catch (RuntimeException e) {
			log.error("Error al confirmar la reserva: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Reactiva una reserva cancelada (vuelve a PENDIENTE)
	 */
	@PutMapping("/{idReserva}/reactivar")
	public ResponseEntity<ReservaDTO> reactivarReserva(@PathVariable("idReserva") Long idReserva) {
		log.info("PUT /api/v1/reservas/{}/reactivar - Reactivando reserva", idReserva);
		try {
			return ResponseEntity.ok(servicioReserva.reactivarReserva(idReserva));
		} catch (RuntimeException e) {
			log.error("Error al reactivar la reserva: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Actualiza una reserva existente
	 */
	@PutMapping("/{idReserva}")
	public ResponseEntity<ReservaDTO> actualizarReserva(@PathVariable("idReserva") Long idReserva,
			@RequestBody Reserva reserva) {
		log.info("PUT /api/v1/reservas/{} - Actualizando reserva", idReserva);
		try {
			ReservaDTO reservaActualizada = servicioReserva.actualizarReserva(idReserva, reserva);
			return ResponseEntity.ok(reservaActualizada);
		} catch (RuntimeException e) {
			log.error("Error al actualizar la reserva: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Cancela una reserva
	 */
	@DeleteMapping("/{idReserva}")
	public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable("idReserva") Long idReserva) {
		log.info("DELETE /api/v1/reservas/{} - Cancelando reserva", idReserva);
		try {
			ReservaDTO reservaCancelada = servicioReserva.cancelarReserva(idReserva);
			return ResponseEntity.ok(reservaCancelada);
		} catch (RuntimeException e) {
			log.error("Error al cancelar la reserva: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

}
