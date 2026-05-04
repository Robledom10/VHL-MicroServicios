package com.hernandolopera.reservation_service.servicios;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hernandolopera.reservation_service.dto.RespuestaConfirmacionDTO;
import com.hernandolopera.reservation_service.dto.SolicitudConfirmacionReservaDTO;
import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;
import com.hernandolopera.reservation_service.repositorios.RepositorioReserva;
import com.hernandolopera.reservation_service.repositorios.RepositorioViajero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServicioConfirmacion {

	private final RepositorioReserva repositorioReserva;
	private final RepositorioViajero repositorioViajero;

	/**
	 * SCRUM-706: Verificar pago y condiciones necesarias
	 * SCRUM-709: Actualizar la reserva al estado Confirmada
	 * SCRUM-711: Guardar la confirmación en el sistema
	 * Confirma una reserva después de validar pago y condiciones
	 */
	public RespuestaConfirmacionDTO confirmarReserva(SolicitudConfirmacionReservaDTO solicitud) {
		log.info("Iniciando proceso de confirmación de reserva: {}", solicitud.getNumeroReserva());

		try {
			// SCRUM-704: Obtener la reserva
			Optional<Reserva> reservaOpcional = repositorioReserva.findById(solicitud.getIdReserva());

			if (reservaOpcional.isEmpty()) {
				log.error("Reserva no encontrada con ID: {}", solicitud.getIdReserva());
				return RespuestaConfirmacionDTO.builder().exito(false).mensaje("Reserva no encontrada")
						.detalleError("No existe una reserva con el ID proporcionado").build();
			}

			Reserva reserva = reservaOpcional.get();

			// SCRUM-706: Verificar pago
			if (!solicitud.getPagoVerificado()) {
				log.warn("Pago no verificado para la reserva: {}", reserva.getNumeroReserva());
				return RespuestaConfirmacionDTO.builder().exito(false).numeroReserva(reserva.getNumeroReserva())
						.estadoReserva(reserva.getEstado().getDescripcion()).mensaje("El pago no ha sido verificado")
						.detalleError("Debe completar el pago antes de confirmar la reserva").build();
			}

			// Verificar que el monto coincida
			if (solicitud.getMontoPago().compareTo(reserva.getPrecioTotal()) != 0) {
				log.warn("Monto de pago incorrecto para la reserva: {}", reserva.getNumeroReserva());
				return RespuestaConfirmacionDTO.builder().exito(false).numeroReserva(reserva.getNumeroReserva())
						.estadoReserva(reserva.getEstado().getDescripcion()).mensaje("El monto del pago no coincide")
						.detalleError("Monto esperado: " + reserva.getPrecioTotal() + ", Monto recibido: "
								+ solicitud.getMontoPago())
						.build();
			}

			// Verificar cantidad de pasajeros
			int cantidadViajeros = repositorioViajero.countByReservaId(reserva.getId());
			if (cantidadViajeros < solicitud.getCantidadPasajeros()) {
				log.warn("Cantidad de viajeros incompleta para la reserva: {}", reserva.getNumeroReserva());
				return RespuestaConfirmacionDTO.builder().exito(false).numeroReserva(reserva.getNumeroReserva())
						.estadoReserva(reserva.getEstado().getDescripcion())
						.mensaje("No hay suficientes viajeros registrados")
						.detalleError("Viajeros registrados: " + cantidadViajeros + ", Requeridos: "
								+ solicitud.getCantidadPasajeros())
						.build();
			}

			// Verificar que el estado actual sea PENDIENTE
			if (!reserva.getEstado().equals(EstadoReserva.PENDIENTE)) {
				log.warn("La reserva no está en estado PENDIENTE: {}", reserva.getNumeroReserva());
				return RespuestaConfirmacionDTO.builder().exito(false).numeroReserva(reserva.getNumeroReserva())
						.estadoReserva(reserva.getEstado().getDescripcion())
						.mensaje("La reserva no se puede confirmar")
						.detalleError("La reserva ya ha sido " + reserva.getEstado().getDescripcion().toLowerCase())
						.build();
			}

			// SCRUM-709: Actualizar estado a CONFIRMADA
			reserva.setEstado(EstadoReserva.CONFIRMADA);
			reserva.setPagoVerificado(true);
			reserva.setFechaConfirmacion(LocalDateTime.now());
			reserva.setFechaActualizacion(LocalDateTime.now());

			// SCRUM-711: Guardar cambios
			Reserva reservaConfirmada = repositorioReserva.save(reserva);
			log.info("Reserva confirmada exitosamente: {}", reservaConfirmada.getNumeroReserva());

			// SCRUM-713: Retornar resultado al frontend
			return RespuestaConfirmacionDTO.builder().exito(true).numeroReserva(reservaConfirmada.getNumeroReserva())
					.estadoReserva(reservaConfirmada.getEstado().getDescripcion())
					.mensaje("Reserva confirmada exitosamente").build();

		} catch (Exception e) {
			log.error("Error al confirmar la reserva", e);
			return RespuestaConfirmacionDTO.builder().exito(false).mensaje("Error en la confirmación de reserva")
					.detalleError(e.getMessage()).build();
		}
	}

}
