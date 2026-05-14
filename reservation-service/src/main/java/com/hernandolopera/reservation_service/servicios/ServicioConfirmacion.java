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

	public RespuestaConfirmacionDTO confirmarReserva(SolicitudConfirmacionReservaDTO solicitud) {
		log.info("Iniciando proceso de confirmacion de reserva: {}", solicitud.getNumeroReserva());

		try {
			Optional<Reserva> reservaOpcional = repositorioReserva.findById(solicitud.getIdReserva());

			if (reservaOpcional.isEmpty()) {
				log.error("Reserva no encontrada con ID: {}", solicitud.getIdReserva());
				return RespuestaConfirmacionDTO.builder()
						.exito(false)
						.mensaje("Reserva no encontrada")
						.detalleError("No existe una reserva con el ID proporcionado")
						.build();
			}

			Reserva reserva = reservaOpcional.get();

			if (!Boolean.TRUE.equals(solicitud.getPagoVerificado())) {
				log.warn("Pago no verificado para la reserva: {}", reserva.getNumeroReserva());
				return respuestaError(reserva, "El pago no ha sido verificado",
						"Debe completar el pago antes de confirmar la reserva");
			}

			if (solicitud.getMontoPago().compareTo(reserva.getPrecioTotal()) != 0) {
				log.warn("Monto de pago incorrecto para la reserva: {}", reserva.getNumeroReserva());
				return respuestaError(reserva, "El monto del pago no coincide",
						"Monto esperado: " + reserva.getPrecioTotal()
								+ ", Monto recibido: " + solicitud.getMontoPago());
			}

			int cantidadPasajeros = reserva.getCantidadPasajeros() != null ? reserva.getCantidadPasajeros() : 0;
			if (cantidadPasajeros < solicitud.getCantidadPasajeros()) {
				log.warn("Cantidad de pasajeros insuficiente para la reserva: {}", reserva.getNumeroReserva());
				return respuestaError(reserva, "Cantidad de pasajeros insuficiente",
						"Pasajeros registrados: " + cantidadPasajeros
								+ ", Requeridos: " + solicitud.getCantidadPasajeros());
			}

			long viajerosRegistrados = repositorioViajero.countByIdReservaAndDatosCompletosTrue(reserva.getId());
			if (viajerosRegistrados < solicitud.getCantidadPasajeros()) {
				log.warn("Viajeros registrados insuficientes para la reserva: {}", reserva.getNumeroReserva());
				return respuestaError(reserva, "No hay suficientes viajeros registrados",
						"Viajeros registrados: " + viajerosRegistrados
								+ ", Requeridos: " + solicitud.getCantidadPasajeros());
			}

			if (!EstadoReserva.PENDIENTE.equals(reserva.getEstado())) {
				log.warn("La reserva no esta en estado PENDIENTE: {}", reserva.getNumeroReserva());
				return respuestaError(reserva, "La reserva no se puede confirmar",
						"La reserva ya ha sido " + reserva.getEstado().getDescripcion().toLowerCase());
			}

			reserva.setEstado(EstadoReserva.CONFIRMADA);
			reserva.setPagoVerificado(true);
			reserva.setFechaConfirmacion(LocalDateTime.now());
			reserva.setFechaActualizacion(LocalDateTime.now());

			Reserva reservaConfirmada = repositorioReserva.save(reserva);
			log.info("Reserva confirmada exitosamente: {}", reservaConfirmada.getNumeroReserva());

			return RespuestaConfirmacionDTO.builder()
					.exito(true)
					.numeroReserva(reservaConfirmada.getNumeroReserva())
					.estadoReserva(reservaConfirmada.getEstado().name())
					.mensaje("Reserva confirmada exitosamente")
					.build();

		} catch (Exception e) {
			log.error("Error al confirmar la reserva", e);
			return RespuestaConfirmacionDTO.builder()
					.exito(false)
					.mensaje("Error en la confirmacion de reserva")
					.detalleError(e.getMessage())
					.build();
		}
	}

	private RespuestaConfirmacionDTO respuestaError(Reserva reserva, String mensaje, String detalleError) {
		return RespuestaConfirmacionDTO.builder()
				.exito(false)
				.numeroReserva(reserva.getNumeroReserva())
				.estadoReserva(reserva.getEstado().name())
				.mensaje(mensaje)
				.detalleError(detalleError)
				.build();
	}
}
