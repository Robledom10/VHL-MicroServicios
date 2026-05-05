package com.hernandolopera.reservation_service.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.reservation_service.dto.RespuestaConfirmacionDTO;
import com.hernandolopera.reservation_service.dto.SolicitudConfirmacionReservaDTO;
import com.hernandolopera.reservation_service.servicios.ServicioConfirmacion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/confirmaciones")
@RequiredArgsConstructor
@Slf4j
public class ControladorConfirmacion {

	private final ServicioConfirmacion servicioConfirmacion;

	/**
	 * SCRUM-649: HU3.3 Confirmar una reserva
	 * SCRUM-706: Verificar pago y condiciones necesarias
	 * SCRUM-709: Actualizar la reserva al estado Confirmada
	 * SCRUM-711: Guardar la confirmación en el sistema
	 * SCRUM-713: Retornar al frontend el resultado del procesamiento
	 * 
	 * Confirma una reserva después de validar pago y condiciones
	 */
	@PostMapping("/confirmar")
	public ResponseEntity<RespuestaConfirmacionDTO> confirmarReserva(
			@Valid @RequestBody SolicitudConfirmacionReservaDTO solicitud) {
		log.info("POST /api/v1/confirmaciones/confirmar - Confirmando reserva: {}", solicitud.getNumeroReserva());

		try {
			RespuestaConfirmacionDTO respuesta = servicioConfirmacion.confirmarReserva(solicitud);

			if (respuesta.getExito()) {
				log.info("Reserva confirmada exitosamente: {}", solicitud.getNumeroReserva());
				return ResponseEntity.ok(respuesta);
			} else {
				log.warn("Error en la confirmación: {}", respuesta.getDetalleError());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
			}
		} catch (Exception e) {
			log.error("Error inesperado al confirmar la reserva", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(RespuestaConfirmacionDTO.builder().exito(false).mensaje("Error interno del servidor")
							.detalleError(e.getMessage()).build());
		}
	}

}
