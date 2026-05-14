package com.hernandolopera.reservation_service.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		RespuestaRegistroViajeroDTO respuesta = servicioViajero.registrarViajero(idReserva, viajeroDTO);
		if (Boolean.TRUE.equals(respuesta.getExito())) {
			return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	}

	@GetMapping("/{idViajero}")
	public ResponseEntity<ViajeroDTO> obtenerViajero(@PathVariable("idViajero") Long idViajero) {
		log.info("GET /api/v1/viajeros/{} - Obteniendo viajero", idViajero);
		return servicioViajero.obtenerViajeroPorId(idViajero)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/reserva/{idReserva}")
	public ResponseEntity<List<ViajeroDTO>> obtenerViajerosPorReserva(@PathVariable("idReserva") Long idReserva) {
		log.info("GET /api/v1/viajeros/reserva/{} - Obteniendo viajeros de reserva", idReserva);
		return ResponseEntity.ok(servicioViajero.obtenerViajerosPorReserva(idReserva));
	}

	@PutMapping("/{idViajero}")
	public ResponseEntity<ViajeroDTO> actualizarViajero(
			@PathVariable("idViajero") Long idViajero,
			@RequestBody ViajeroDTO viajeroDTO) {
		log.info("PUT /api/v1/viajeros/{} - Actualizando viajero", idViajero);
		return servicioViajero.actualizarViajero(idViajero, viajeroDTO)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
