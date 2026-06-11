package com.hernandolopera.reservation_service.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hernandolopera.reservation_service.dto.ReservaDTO;
import com.hernandolopera.reservation_service.dto.ViajeroDTO;
import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;
import com.hernandolopera.reservation_service.entidades.Viajero;
import com.hernandolopera.reservation_service.repositorios.RepositorioReserva;
import com.hernandolopera.reservation_service.repositorios.RepositorioViajero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServicioReserva {

	private final RepositorioReserva repositorioReserva;
	private final RepositorioViajero repositorioViajero;

	/**
	 * SCRUM-704: Crear servicio para obtener reservas
	 * Obtiene una reserva por su ID
	 */
	public Optional<ReservaDTO> obtenerReservaPorId(Long idReserva) {
		log.info("Obteniendo reserva con ID: {}", idReserva);
		actualizarReservasPasadas();
		return repositorioReserva.findById(idReserva).map(this::convertirADTO);
	}

	/**
	 * SCRUM-704: Crear servicio para obtener reservas
	 * Obtiene una reserva por su número de reserva
	 */
	public Optional<ReservaDTO> obtenerReservaPorNumero(String numeroReserva) {
		log.info("Obteniendo reserva con número: {}", numeroReserva);
		actualizarReservasPasadas();
		return repositorioReserva.findByNumeroReserva(numeroReserva).map(this::convertirADTO);
	}

	/**
	 * Obtiene todas las reservas de un usuario
	 */
	public List<ReservaDTO> obtenerReservasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByIdUsuario(idUsuario).stream().map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	/**
	 * Obtiene todas las reservas de un paquete
	 */
	public List<ReservaDTO> obtenerReservasPorPaquete(Long idPaquete) {
		log.info("Obteniendo reservas del paquete: {}", idPaquete);
		actualizarReservasPasadas();
		return repositorioReserva.findByIdPaquete(idPaquete).stream().map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	/**
	 * Obtiene todas las reservas por estado
	 */
	public List<ReservaDTO> obtenerReservasPorEstado(EstadoReserva estado) {
		log.info("Obteniendo reservas con estado: {}", estado);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstado(estado).stream().map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	/**
	 * SCRUM-704: Crear servicio para obtener reservas
	 * Obtiene todas las reservas confirmadas de un usuario
	 */
	public List<ReservaDTO> obtenerReservasConfirmadasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas confirmadas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstadoAndIdUsuario(EstadoReserva.CONFIRMADA, idUsuario).stream()
				.map(this::convertirADTO).collect(Collectors.toList());
	}

	/**
	 * Obtiene las reservas pasadas de un usuario
	 */
	public List<ReservaDTO> obtenerReservasPasadasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas pasadas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstadoAndIdUsuario(EstadoReserva.PASADA, idUsuario).stream()
				.map(this::convertirADTO).collect(Collectors.toList());
	}

	/**
	 * Crea una nueva reserva
	 */
	public ReservaDTO crearReserva(Reserva reserva) {
		log.info("Creando nueva reserva para el usuario: {}", reserva.getIdUsuario());

		reserva.setNumeroReserva(generarNumeroReserva());
		reserva.setEstado(EstadoReserva.PENDIENTE);
		reserva.setFechaCreacion(LocalDate.now());
		reserva.setPagoVerificado(false);

		Reserva reservaGuardada = repositorioReserva.save(reserva);
		log.info("Reserva creada con número: {}", reservaGuardada.getNumeroReserva());

		return convertirADTO(reservaGuardada);
	}

	/**
	 * Actualiza una reserva existente
	 */
	public ReservaDTO actualizarReserva(Long idReserva, Reserva reservaActualizar) {
		log.info("Actualizando reserva con ID: {}", idReserva);

		Optional<Reserva> reservaExistente = repositorioReserva.findById(idReserva);

		if (reservaExistente.isEmpty()) {
			log.error("Reserva no encontrada con ID: {}", idReserva);
			throw new RuntimeException("Reserva no encontrada");
		}

		Reserva reserva = reservaExistente.get();
		reserva.setFechaActualizacion(LocalDateTime.now());

		if (reservaActualizar.getPrecioTotal() != null) {
			reserva.setPrecioTotal(reservaActualizar.getPrecioTotal());
		}

		if (reservaActualizar.getCantidadPasajeros() != null) {
			reserva.setCantidadPasajeros(reservaActualizar.getCantidadPasajeros());
		}

		if (reservaActualizar.getFechaInicioViaje() != null) {
			reserva.setFechaInicioViaje(reservaActualizar.getFechaInicioViaje());
		}

		if (reservaActualizar.getFechaFinViaje() != null) {
			reserva.setFechaFinViaje(reservaActualizar.getFechaFinViaje());
		}

		if (reservaActualizar.getEstado() != null) {
			reserva.setEstado(reservaActualizar.getEstado());
		}

		if (reservaActualizar.getPagoVerificado() != null) {
			reserva.setPagoVerificado(reservaActualizar.getPagoVerificado());
		}

		if (reservaActualizar.getFechaConfirmacion() != null) {
			reserva.setFechaConfirmacion(reservaActualizar.getFechaConfirmacion());
		}

		if (reservaActualizar.getNotas() != null) {
			reserva.setNotas(reservaActualizar.getNotas());
		}

		Reserva reservaActualizada = repositorioReserva.save(reserva);
		log.info("Reserva actualizada: {}", reservaActualizada.getNumeroReserva());

		return convertirADTO(reservaActualizada);
	}

	/**
	 * Cancela una reserva
	 */
	public ReservaDTO cancelarReserva(Long idReserva) {
		log.info("Cancelando reserva con ID: {}", idReserva);

		Optional<Reserva> reservaOpcional = repositorioReserva.findById(idReserva);

		if (reservaOpcional.isEmpty()) {
			log.error("Reserva no encontrada con ID: {}", idReserva);
			throw new RuntimeException("Reserva no encontrada");
		}

		Reserva reserva = reservaOpcional.get();
		reserva.setEstado(EstadoReserva.CANCELADA);
		reserva.setFechaActualizacion(LocalDateTime.now());

		Reserva reservaActualizada = repositorioReserva.save(reserva);
		log.info("Reserva cancelada: {}", reservaActualizada.getNumeroReserva());

		return convertirADTO(reservaActualizada);
	}

	/**
	 * Genera un número de reserva único
	 */
	private String generarNumeroReserva() {
		return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private void actualizarReservasPasadas() {
		List<Reserva> reservasPasadas = repositorioReserva.findByEstadoAndFechaFinViajeBefore(
				EstadoReserva.CONFIRMADA, LocalDateTime.now());

		if (reservasPasadas.isEmpty()) {
			return;
		}

		LocalDateTime fechaActualizacion = LocalDateTime.now();
		reservasPasadas.forEach(reserva -> {
			reserva.setEstado(EstadoReserva.PASADA);
			reserva.setFechaActualizacion(fechaActualizacion);
		});

		repositorioReserva.saveAll(reservasPasadas);
		log.info("Se marcaron {} reserva(s) como pasadas", reservasPasadas.size());
	}

	/**
	 * Convierte una entidad Reserva a DTO
	 */
	private ReservaDTO convertirADTO(Reserva reserva) {
		List<ViajeroDTO> viajeros = repositorioViajero.findByIdReserva(reserva.getId()).stream()
				.map(this::convertirViajeroADTO)
				.collect(Collectors.toList());

		return ReservaDTO.builder().id(reserva.getId()).numeroReserva(reserva.getNumeroReserva())
				.idPaquete(reserva.getIdPaquete()).idUsuario(reserva.getIdUsuario())
				.cantidadPasajeros(reserva.getCantidadPasajeros()).precioTotal(reserva.getPrecioTotal())
				.fechaInicioViaje(reserva.getFechaInicioViaje()).fechaFinViaje(reserva.getFechaFinViaje())
				.estado(reserva.getEstado()).pagoVerificado(reserva.getPagoVerificado())
				.fechaCreacion(reserva.getFechaCreacion() != null ? reserva.getFechaCreacion().atStartOfDay() : null)
				.fechaActualizacion(reserva.getFechaActualizacion())
				.fechaConfirmacion(reserva.getFechaConfirmacion()).viajeros(viajeros).notas(reserva.getNotas()).build();
	}

	private ViajeroDTO convertirViajeroADTO(Viajero viajero) {
		return ViajeroDTO.builder()
				.id(viajero.getId())
				.nombre(viajero.getNombre())
				.apellido(viajero.getApellido())
				.documento(viajero.getDocumento())
				.tipoDocumento(viajero.getTipoDocumento())
				.fechaNacimiento(viajero.getFechaNacimiento())
				.email(viajero.getEmail())
				.telefono(viajero.getTelefono())
				.genero(viajero.getGenero())
				.nacionalidad(viajero.getNacionalidad())
				.datosCompletos(viajero.getDatosCompletos())
				.documentosVerificados(viajero.getDocumentosVerificados())
				.fechaCreacion(viajero.getFechaCreacion())
				.fechaActualizacion(viajero.getFechaActualizacion())
				.build();
	}

}
