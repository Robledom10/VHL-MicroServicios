package com.hernandolopera.reservation_service.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hernandolopera.reservation_service.dto.ViajeroDTO;
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
public class ServicioViajero {

	private final RepositorioViajero repositorioViajero;
	private final RepositorioReserva repositorioReserva;

	/**
	 * SCRUM-917: Crear servicio para recibir datos de viajeros
	 * Registra un nuevo viajero en una reserva
	 */
	public ViajeroDTO registrarViajero(Long idReserva, ViajeroDTO viajeroDTO) {
		log.info("Registrando nuevo viajero en la reserva: {}", idReserva);

		Optional<Reserva> reservaOpcional = repositorioReserva.findById(idReserva);

		if (reservaOpcional.isEmpty()) {
			log.error("Reserva no encontrada con ID: {}", idReserva);
			throw new RuntimeException("Reserva no encontrada");
		}

		Reserva reserva = reservaOpcional.get();

		// SCRUM-919: Verificar que la cantidad de viajeros no exceda el límite
		int cantidadActual = repositorioViajero.countByReservaId(idReserva);
		if (cantidadActual >= reserva.getCantidadPasajeros()) {
			log.error("Ya se han registrado todos los viajeros para la reserva: {}", idReserva);
			throw new RuntimeException("Se ha alcanzado el máximo número de viajeros para esta reserva");
		}

		// Crear nueva entidad Viajero
		Viajero viajero = Viajero.builder().nombre(viajeroDTO.getNombre()).apellido(viajeroDTO.getApellido())
				.documento(viajeroDTO.getDocumento()).tipoDocumento(viajeroDTO.getTipoDocumento())
				.fechaNacimiento(viajeroDTO.getFechaNacimiento()).email(viajeroDTO.getEmail())
				.telefono(viajeroDTO.getTelefono()).genero(viajeroDTO.getGenero())
				.nacionalidad(viajeroDTO.getNacionalidad()).datosCompletos(true).documentosVerificados(false)
				.fechaCreacion(LocalDateTime.now()).reserva(reserva).build();

		Viajero viajeroGuardado = repositorioViajero.save(viajero);
		log.info("Viajero registrado exitosamente con ID: {}", viajeroGuardado.getId());

		return convertirADTO(viajeroGuardado);
	}

	/**
	 * SCRUM-918: Crear servicio para recibir datos de viajeros (actualizar)
	 * Actualiza los datos de un viajero existente
	 */
	public ViajeroDTO actualizarViajero(Long idViajero, ViajeroDTO viajeroDTO) {
		log.info("Actualizando viajero con ID: {}", idViajero);

		Optional<Viajero> viajeroOpcional = repositorioViajero.findById(idViajero);

		if (viajeroOpcional.isEmpty()) {
			log.error("Viajero no encontrado con ID: {}", idViajero);
			throw new RuntimeException("Viajero no encontrado");
		}

		Viajero viajero = viajeroOpcional.get();
		viajero.setNombre(viajeroDTO.getNombre());
		viajero.setApellido(viajeroDTO.getApellido());
		viajero.setDocumento(viajeroDTO.getDocumento());
		viajero.setTipoDocumento(viajeroDTO.getTipoDocumento());
		viajero.setFechaNacimiento(viajeroDTO.getFechaNacimiento());
		viajero.setEmail(viajeroDTO.getEmail());
		viajero.setTelefono(viajeroDTO.getTelefono());
		viajero.setGenero(viajeroDTO.getGenero());
		viajero.setNacionalidad(viajeroDTO.getNacionalidad());
		viajero.setFechaActualizacion(LocalDateTime.now());

		Viajero viajeroActualizado = repositorioViajero.save(viajero);
		log.info("Viajero actualizado exitosamente: {}", viajeroActualizado.getId());

		return convertirADTO(viajeroActualizado);
	}

	/**
	 * Obtiene todos los viajeros de una reserva
	 */
	public List<ViajeroDTO> obtenerViajerosPorReserva(Long idReserva) {
		log.info("Obteniendo viajeros de la reserva: {}", idReserva);
		return repositorioViajero.findByReservaId(idReserva).stream().map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	/**
	 * Obtiene un viajero por su ID
	 */
	public Optional<ViajeroDTO> obtenerViajero(Long idViajero) {
		log.info("Obteniendo viajero con ID: {}", idViajero);
		return repositorioViajero.findById(idViajero).map(this::convertirADTO);
	}

	/**
	 * Elimina un viajero
	 */
	public void eliminarViajero(Long idViajero) {
		log.info("Eliminando viajero con ID: {}", idViajero);
		repositorioViajero.deleteById(idViajero);
	}

	/**
	 * Convierte una entidad Viajero a DTO
	 */
	private ViajeroDTO convertirADTO(Viajero viajero) {
		return ViajeroDTO.builder().id(viajero.getId()).nombre(viajero.getNombre()).apellido(viajero.getApellido())
				.documento(viajero.getDocumento()).tipoDocumento(viajero.getTipoDocumento())
				.fechaNacimiento(viajero.getFechaNacimiento()).email(viajero.getEmail())
				.telefono(viajero.getTelefono()).genero(viajero.getGenero())
				.nacionalidad(viajero.getNacionalidad()).datosCompletos(viajero.getDatosCompletos())
				.documentosVerificados(viajero.getDocumentosVerificados()).fechaCreacion(viajero.getFechaCreacion())
				.fechaActualizacion(viajero.getFechaActualizacion()).build();
	}

}
