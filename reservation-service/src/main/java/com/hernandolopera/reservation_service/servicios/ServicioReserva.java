package com.hernandolopera.reservation_service.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hernandolopera.reservation_service.clientes.ClienteAuth;
import com.hernandolopera.reservation_service.clientes.ClienteCatalogo;
import com.hernandolopera.reservation_service.clientes.ClienteOperaciones;
import com.hernandolopera.reservation_service.dto.AcompananteDTO;
import com.hernandolopera.reservation_service.dto.ContactoEmergenciaDTO;
import com.hernandolopera.reservation_service.dto.ReservaDTO;
import com.hernandolopera.reservation_service.dto.SolicitudCrearReserva;
import com.hernandolopera.reservation_service.dto.UsuarioDTO;
import com.hernandolopera.reservation_service.dto.ViajeroDTO;
import com.hernandolopera.reservation_service.entidades.Acompanante;
import com.hernandolopera.reservation_service.entidades.ContactoEmergenciaReserva;
import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;
import com.hernandolopera.reservation_service.entidades.Viajero;
import com.hernandolopera.reservation_service.repositorios.RepositorioAcompanante;
import com.hernandolopera.reservation_service.repositorios.RepositorioContactoEmergenciaReserva;
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
	private final RepositorioAcompanante repositorioAcompanante;
	private final RepositorioContactoEmergenciaReserva repositorioContactoEmergencia;
	private final ClienteAuth clienteAuth;
	private final ClienteOperaciones clienteOperaciones;
	private final ClienteCatalogo clienteCatalogo;
	private final ServicioEmail servicioEmail;

	// ─── Obtener todas las reservas (panel admin) — paginado ─────────────────

	@Transactional(readOnly = true)
	public Page<ReservaDTO> obtenerTodasLasReservas(int pagina, int tamano) {
		log.info("Obteniendo reservas — página {} tamaño {}", pagina, tamano);
		actualizarReservasPasadas();
		return repositorioReserva
				.findAll(PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "id")))
				.map(this::convertirADTO);
	}

	// ─── Obtener por ID ───────────────────────────────────────────────────────

	@Transactional(readOnly = true)
	public Optional<ReservaDTO> obtenerReservaPorId(Long idReserva) {
		log.info("Obteniendo reserva con ID: {}", idReserva);
		actualizarReservasPasadas();
		return repositorioReserva.findById(idReserva).map(this::convertirADTO);
	}

	// ─── Obtener por número ───────────────────────────────────────────────────

	@Transactional(readOnly = true)
	public Optional<ReservaDTO> obtenerReservaPorNumero(String numeroReserva) {
		log.info("Obteniendo reserva con número: {}", numeroReserva);
		actualizarReservasPasadas();
		return repositorioReserva.findByNumeroReserva(numeroReserva).map(this::convertirADTO);
	}

	// ─── Obtener por usuario ──────────────────────────────────────────────────

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByIdUsuario(idUsuario).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasPorPaquete(Long idPaquete) {
		log.info("Obteniendo reservas del paquete: {}", idPaquete);
		actualizarReservasPasadas();
		return repositorioReserva.findByIdPaquete(idPaquete).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasPorViaje(Long idViaje) {
		log.info("Obteniendo reservas del viaje: {}", idViaje);
		actualizarReservasPasadas();
		return repositorioReserva.findByIdViaje(idViaje).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasPorEstado(EstadoReserva estado) {
		log.info("Obteniendo reservas con estado: {}", estado);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstado(estado).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	// ─── Obtener confirmadas del usuario ──────────────────────────────────────

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasConfirmadasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas confirmadas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstadoAndIdUsuario(EstadoReserva.CONFIRMADA, idUsuario).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	// ─── Obtener pasadas del usuario ──────────────────────────────────────────

	@Transactional(readOnly = true)
	public List<ReservaDTO> obtenerReservasPasadasDelUsuario(Long idUsuario) {
		log.info("Obteniendo reservas pasadas del usuario: {}", idUsuario);
		actualizarReservasPasadas();
		return repositorioReserva.findByEstadoAndIdUsuario(EstadoReserva.PASADA, idUsuario).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	// ─── Crear reserva ────────────────────────────────────────────────────────

	public ReservaDTO crearReserva(SolicitudCrearReserva solicitud) {
		log.info("Creando reserva para usuario ID: {}", solicitud.getIdUsuario());

		String destino = clienteCatalogo.obtenerDestino(solicitud.getIdPaquete());

		Reserva reserva = Reserva.builder()
				.numeroReserva(generarNumeroReserva())
				.estado(EstadoReserva.PENDIENTE)
				.fechaCreacion(LocalDate.now())
				.pagoVerificado(false)
				.idUsuario(solicitud.getIdUsuario())
				.idPaquete(solicitud.getIdPaquete())
				.cantidadPasajeros(solicitud.getPersonas())
				.precioTotal(solicitud.getTotal())
				.fechaInicioViaje(parsearFecha(solicitud.getFechaSalida()))
				.fechaFinViaje(parsearFecha(solicitud.getFechaRegreso()))
				.notas(solicitud.getNotas())
				.paqueteNombre(solicitud.getPaqueteNombre())
				.destino(destino)
				.tipoHabitacion(solicitud.getTipoHabitacion())
				.solicitudEspecial(solicitud.getSolicitudEspecial())
				.idViaje(solicitud.getIdViaje())
				.build();

		Reserva guardada = repositorioReserva.save(reserva);

		if (solicitud.getAcompanantes() != null) {
			solicitud.getAcompanantes().forEach(aDTO -> {
				Acompanante acompanante = Acompanante.builder()
						.idReserva(guardada.getId())
						.nombre(aDTO.getNombre())
						.tipoDocumento(aDTO.getTipoDocumento())
						.documento(aDTO.getDocumento())
						.fechaNacimiento(aDTO.getFechaNacimiento() != null && !aDTO.getFechaNacimiento().isBlank()
								? LocalDate.parse(aDTO.getFechaNacimiento()) : null)
						.build();
				repositorioAcompanante.save(acompanante);
			});
		}

		if (solicitud.getContactosEmergencia() != null) {
			solicitud.getContactosEmergencia().forEach(cDTO -> {
				ContactoEmergenciaReserva contacto = ContactoEmergenciaReserva.builder()
						.idReserva(guardada.getId())
						.nombre(cDTO.getNombre())
						.parentesco(cDTO.getParentesco())
						.telefono(cDTO.getTelefono())
						.correo(cDTO.getCorreo())
						.build();
				repositorioContactoEmergencia.save(contacto);
			});

			if (solicitud.getIdViaje() != null && !solicitud.getContactosEmergencia().isEmpty()) {
				clienteOperaciones.enviarContactosEmergencia(solicitud.getIdViaje(), solicitud.getContactosEmergencia());
			}
		}

		log.info("Reserva {} creada exitosamente", guardada.getNumeroReserva());
		ReservaDTO dto = convertirADTO(guardada);
		servicioEmail.enviarConfirmacionReserva(dto);
		return dto;
	}

	// ─── Notificar reserva (admin) ────────────────────────────────────────────

	public void notificarReserva(Long idReserva, String asunto, String mensaje) {
		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
		ReservaDTO dto = convertirADTO(reserva);
		servicioEmail.enviarNotificacionAdmin(dto, asunto, mensaje);
		log.info("Notificación admin enviada para reserva {}", dto.getNumeroReserva());
	}

	// ─── Actualizar reserva ───────────────────────────────────────────────────

	public ReservaDTO actualizarReserva(Long idReserva, Reserva reservaActualizar) {
		log.info("Actualizando reserva con ID: {}", idReserva);

		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

		reserva.setFechaActualizacion(LocalDateTime.now());

		if (reservaActualizar.getPrecioTotal() != null) reserva.setPrecioTotal(reservaActualizar.getPrecioTotal());
		if (reservaActualizar.getCantidadPasajeros() != null) reserva.setCantidadPasajeros(reservaActualizar.getCantidadPasajeros());
		if (reservaActualizar.getFechaInicioViaje() != null) reserva.setFechaInicioViaje(reservaActualizar.getFechaInicioViaje());
		if (reservaActualizar.getFechaFinViaje() != null) reserva.setFechaFinViaje(reservaActualizar.getFechaFinViaje());
		if (reservaActualizar.getEstado() != null) reserva.setEstado(reservaActualizar.getEstado());
		if (reservaActualizar.getPagoVerificado() != null) reserva.setPagoVerificado(reservaActualizar.getPagoVerificado());
		if (reservaActualizar.getFechaConfirmacion() != null) reserva.setFechaConfirmacion(reservaActualizar.getFechaConfirmacion());
		if (reservaActualizar.getNotas() != null) reserva.setNotas(reservaActualizar.getNotas());

		return convertirADTO(repositorioReserva.save(reserva));
	}

	// ─── Confirmar reserva directamente (panel admin) ─────────────────────────

	public ReservaDTO confirmarReserva(Long idReserva) {
		log.info("Confirmando reserva ID: {}", idReserva);
		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
		reserva.setEstado(EstadoReserva.CONFIRMADA);
		reserva.setFechaConfirmacion(LocalDateTime.now());
		reserva.setFechaActualizacion(LocalDateTime.now());
		return convertirADTO(repositorioReserva.save(reserva));
	}

	// ─── Cancelar reserva ─────────────────────────────────────────────────────

	public ReservaDTO cancelarReserva(Long idReserva) {
		log.info("Cancelando reserva ID: {}", idReserva);
		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
		reserva.setEstado(EstadoReserva.CANCELADA);
		reserva.setFechaActualizacion(LocalDateTime.now());
		return convertirADTO(repositorioReserva.save(reserva));
	}

	// ─── Reactivar reserva (cancelada → pendiente) ────────────────────────────

	public ReservaDTO reactivarReserva(Long idReserva) {
		log.info("Reactivando reserva ID: {}", idReserva);
		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
		reserva.setEstado(EstadoReserva.PENDIENTE);
		reserva.setFechaActualizacion(LocalDateTime.now());
		return convertirADTO(repositorioReserva.save(reserva));
	}

	// ─── Contactos de emergencia por viaje (para panel de operaciones) ───────

	@Transactional(readOnly = true)
	public List<ContactoEmergenciaDTO> obtenerContactosPorViaje(Long idViaje) {
		log.info("Obteniendo contactos de emergencia para viaje ID: {}", idViaje);
		return repositorioContactoEmergencia.findByIdViaje(idViaje).stream()
				.map(c -> ContactoEmergenciaDTO.builder()
						.id(c.getId())
						.nombre(c.getNombre())
						.parentesco(c.getParentesco())
						.telefono(c.getTelefono())
						.correo(c.getCorreo())
						.build())
				.collect(Collectors.toList());
	}

	// ─── Helpers privados ─────────────────────────────────────────────────────

	private LocalDateTime parsearFecha(String fecha) {
		if (fecha == null || fecha.isBlank()) return null;
		String soloFecha = fecha.contains("T") ? fecha.split("T")[0] : fecha.split(" ")[0];
		return LocalDate.parse(soloFecha).atStartOfDay();
	}

	private String generarNumeroReserva() {
		return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private void actualizarReservasPasadas() {
		List<Reserva> pasadas = repositorioReserva.findByEstadoAndFechaFinViajeBefore(
				EstadoReserva.CONFIRMADA, LocalDateTime.now());
		if (pasadas.isEmpty()) return;
		LocalDateTime ahora = LocalDateTime.now();
		pasadas.forEach(r -> {
			r.setEstado(EstadoReserva.PASADA);
			r.setFechaActualizacion(ahora);
		});
		repositorioReserva.saveAll(pasadas);
		log.info("Se marcaron {} reserva(s) como pasadas", pasadas.size());
	}

	// ─── Conversión a DTO ─────────────────────────────────────────────────────

	ReservaDTO convertirADTO(Reserva reserva) {
		List<ViajeroDTO> viajeros = repositorioViajero.findByIdReserva(reserva.getId()).stream()
				.map(this::convertirViajeroADTO)
				.collect(Collectors.toList());

		List<AcompananteDTO> acompanantes = repositorioAcompanante.findByIdReserva(reserva.getId()).stream()
				.map(a -> AcompananteDTO.builder()
						.id(a.getId())
						.nombre(a.getNombre())
						.tipoDocumento(a.getTipoDocumento())
						.documento(a.getDocumento())
						.fechaNacimiento(a.getFechaNacimiento() != null ? a.getFechaNacimiento().toString() : null)
						.build())
				.collect(Collectors.toList());

		List<ContactoEmergenciaDTO> contactosEmergencia = repositorioContactoEmergencia
				.findByIdReserva(reserva.getId()).stream()
				.map(c -> ContactoEmergenciaDTO.builder()
						.id(c.getId())
						.nombre(c.getNombre())
						.parentesco(c.getParentesco())
						.telefono(c.getTelefono())
						.correo(c.getCorreo())
						.build())
				.collect(Collectors.toList());

		UsuarioDTO datosUsuario = reserva.getIdUsuario() != null
				? clienteAuth.obtenerUsuarioPorId(reserva.getIdUsuario()).orElse(null)
				: null;

		String fechaViaje = "";
		if (reserva.getFechaInicioViaje() != null) {
			LocalDate f = reserva.getFechaInicioViaje().toLocalDate();
			fechaViaje = String.format("%02d - %02d - %d", f.getDayOfMonth(), f.getMonthValue(), f.getYear());
		}

		String fechaReserva = reserva.getFechaCreacion() != null ? reserva.getFechaCreacion().toString() : "";
		String estadoDesc = reserva.getEstado() != null ? reserva.getEstado().getDescripcion() : "";

		return ReservaDTO.builder()
				.id(reserva.getId())
				.numeroReserva(reserva.getNumeroReserva())
				.idPaquete(reserva.getIdPaquete())
				.idViaje(reserva.getIdViaje())
				.idUsuario(reserva.getIdUsuario())
				.cantidadPasajeros(reserva.getCantidadPasajeros())
				.precioTotal(reserva.getPrecioTotal())
				.fechaInicioViaje(reserva.getFechaInicioViaje())
				.fechaFinViaje(reserva.getFechaFinViaje())
				.estado(reserva.getEstado())
				.pagoVerificado(reserva.getPagoVerificado())
				.fechaCreacion(reserva.getFechaCreacion() != null ? reserva.getFechaCreacion().atStartOfDay() : null)
				.fechaActualizacion(reserva.getFechaActualizacion())
				.fechaConfirmacion(reserva.getFechaConfirmacion())
				.notas(reserva.getNotas())
				.viajeros(viajeros)
				.datosUsuario(datosUsuario)
				.paqueteNombre(reserva.getPaqueteNombre())
				.destino(reserva.getDestino())
				.tipoHabitacion(reserva.getTipoHabitacion())
				.solicitudEspecial(reserva.getSolicitudEspecial())
				.contactosEmergencia(contactosEmergencia)
				.acompanantes(acompanantes)
				.personas(reserva.getCantidadPasajeros())
				.total(reserva.getPrecioTotal())
				.fechaViaje(fechaViaje)
				.fechaReserva(fechaReserva)
				.estadoDescripcion(estadoDesc)
				.build();
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
