package com.hernandolopera.reservation_service.controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.reservation_service.dto.RespuestaRegistroViajeroDTO;
import com.hernandolopera.reservation_service.dto.ViajeroDTO;
import com.hernandolopera.reservation_service.servicios.ServicioViajero;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/viajeros")
@RequiredArgsConstructor
@Slf4j
public class ControladorViajero {

	private final ServicioViajero servicioViajero;

	@PostMapping("/reserva/{idReserva}")
	public ResponseEntity<RespuestaRegistroViajeroDTO> registrarViajero(
			@PathVariable("idReserva") Long idReserva,
			@Valid @RequestBody ViajeroDTO viajeroDTO) {
		log.info("POST /api/v1/viajeros/reserva/{} - Registrando viajero", idReserva);

		try {
			ViajeroDTO viajeroRegistrado = servicioViajero.registrarViajero(idReserva, viajeroDTO);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(RespuestaRegistroViajeroDTO.builder()
							.exito(true)
							.mensaje("Viajero registrado exitosamente")
							.idViajero(viajeroRegistrado.getId())
							.idReserva(idReserva)
							.build());
		} catch (RuntimeException e) {
			log.error("Error al registrar el viajero: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(RespuestaRegistroViajeroDTO.builder()
							.exito(false)
							.mensaje("Error al registrar viajero")
							.detalleError(e.getMessage())
							.idReserva(idReserva)
							.build());
		}
	}

	@GetMapping("/{idViajero}")
	public ResponseEntity<ViajeroDTO> obtenerViajero(@PathVariable("idViajero") Long idViajero) {
		log.info("GET /api/v1/viajeros/{} - Obteniendo viajero", idViajero);
		Optional<ViajeroDTO> viajero = servicioViajero.obtenerViajero(idViajero);
		return viajero.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/reserva/{idReserva}")
	public ResponseEntity<List<ViajeroDTO>> obtenerViajerosPorReserva(@PathVariable("idReserva") Long idReserva) {
		log.info("GET /api/v1/viajeros/reserva/{} - Obteniendo viajeros de reserva", idReserva);
		return ResponseEntity.ok(servicioViajero.obtenerViajerosPorReserva(idReserva));
	}

	@PutMapping("/{idViajero}")
	public ResponseEntity<ViajeroDTO> actualizarViajero(
			@PathVariable("idViajero") Long idViajero,
			@Valid @RequestBody ViajeroDTO viajeroDTO) {
		log.info("PUT /api/v1/viajeros/{} - Actualizando viajero", idViajero);

		try {
			return ResponseEntity.ok(servicioViajero.actualizarViajero(idViajero, viajeroDTO));
		} catch (RuntimeException e) {
			log.error("Error al actualizar el viajero: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{idViajero}")
	public ResponseEntity<Void> eliminarViajero(@PathVariable("idViajero") Long idViajero) {
		log.info("DELETE /api/v1/viajeros/{} - Eliminando viajero", idViajero);

		try {
			servicioViajero.eliminarViajero(idViajero);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Error al eliminar el viajero: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
