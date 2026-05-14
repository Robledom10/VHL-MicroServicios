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

	public ViajeroDTO registrarViajero(Long idReserva, ViajeroDTO viajeroDTO) {
		log.info("Registrando viajero para reserva: {}", idReserva);

		Reserva reserva = repositorioReserva.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

		long viajerosRegistrados = repositorioViajero.countByIdReserva(idReserva);
		if (reserva.getCantidadPasajeros() != null && viajerosRegistrados >= reserva.getCantidadPasajeros()) {
			throw new RuntimeException("Se ha alcanzado el maximo numero de viajeros para esta reserva");
		}

		Viajero viajero = convertirAEntidad(viajeroDTO);
		viajero.setIdReserva(idReserva);
		viajero.setDatosCompletos(tieneDatosObligatorios(viajero));
		viajero.setDocumentosVerificados(false);
		viajero.setFechaCreacion(LocalDateTime.now());

		Viajero viajeroGuardado = repositorioViajero.save(viajero);
		log.info("Viajero registrado exitosamente con ID: {}", viajeroGuardado.getId());

		return convertirADTO(viajeroGuardado);
	}

	@Transactional(readOnly = true)
	public Optional<ViajeroDTO> obtenerViajero(Long idViajero) {
		return repositorioViajero.findById(idViajero).map(this::convertirADTO);
	}

	@Transactional(readOnly = true)
	public List<ViajeroDTO> obtenerViajerosPorReserva(Long idReserva) {
		return repositorioViajero.findByIdReserva(idReserva).stream()
				.map(this::convertirADTO)
				.collect(Collectors.toList());
	}

	public ViajeroDTO actualizarViajero(Long idViajero, ViajeroDTO viajeroDTO) {
		Viajero viajero = repositorioViajero.findById(idViajero)
				.orElseThrow(() -> new RuntimeException("Viajero no encontrado"));

		actualizarCampos(viajero, viajeroDTO);
		viajero.setDatosCompletos(tieneDatosObligatorios(viajero));
		viajero.setFechaActualizacion(LocalDateTime.now());

		Viajero viajeroActualizado = repositorioViajero.save(viajero);
		log.info("Viajero actualizado exitosamente: {}", viajeroActualizado.getId());

		return convertirADTO(viajeroActualizado);
	}

	public void eliminarViajero(Long idViajero) {
		log.info("Eliminando viajero con ID: {}", idViajero);
		if (!repositorioViajero.existsById(idViajero)) {
			throw new RuntimeException("Viajero no encontrado");
		}
		repositorioViajero.deleteById(idViajero);
	}

	private void actualizarCampos(Viajero viajero, ViajeroDTO viajeroDTO) {
		if (viajeroDTO.getNombre() != null) {
			viajero.setNombre(viajeroDTO.getNombre());
		}
		if (viajeroDTO.getApellido() != null) {
			viajero.setApellido(viajeroDTO.getApellido());
		}
		if (viajeroDTO.getDocumento() != null) {
			viajero.setDocumento(viajeroDTO.getDocumento());
		}
		if (viajeroDTO.getTipoDocumento() != null) {
			viajero.setTipoDocumento(viajeroDTO.getTipoDocumento());
		}
		if (viajeroDTO.getFechaNacimiento() != null) {
			viajero.setFechaNacimiento(viajeroDTO.getFechaNacimiento());
		}
		if (viajeroDTO.getEmail() != null) {
			viajero.setEmail(viajeroDTO.getEmail());
		}
		if (viajeroDTO.getTelefono() != null) {
			viajero.setTelefono(viajeroDTO.getTelefono());
		}
		if (viajeroDTO.getGenero() != null) {
			viajero.setGenero(viajeroDTO.getGenero());
		}
		if (viajeroDTO.getNacionalidad() != null) {
			viajero.setNacionalidad(viajeroDTO.getNacionalidad());
		}
		if (viajeroDTO.getDocumentosVerificados() != null) {
			viajero.setDocumentosVerificados(viajeroDTO.getDocumentosVerificados());
		}
	}

	private Viajero convertirAEntidad(ViajeroDTO viajeroDTO) {
		return Viajero.builder()
				.nombre(viajeroDTO.getNombre())
				.apellido(viajeroDTO.getApellido())
				.documento(viajeroDTO.getDocumento())
				.tipoDocumento(viajeroDTO.getTipoDocumento())
				.fechaNacimiento(viajeroDTO.getFechaNacimiento())
				.email(viajeroDTO.getEmail())
				.telefono(viajeroDTO.getTelefono())
				.genero(viajeroDTO.getGenero())
				.nacionalidad(viajeroDTO.getNacionalidad())
				.build();
	}

	private ViajeroDTO convertirADTO(Viajero viajero) {
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

	private boolean tieneDatosObligatorios(Viajero viajero) {
		return tieneTexto(viajero.getNombre())
				&& tieneTexto(viajero.getApellido())
				&& tieneTexto(viajero.getDocumento())
				&& tieneTexto(viajero.getTipoDocumento())
				&& viajero.getFechaNacimiento() != null
				&& tieneTexto(viajero.getEmail());
	}

	private boolean tieneTexto(String valor) {
		return valor != null && !valor.isBlank();
	}
}
