package com.hernandolopera.reservation_service.clientes;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.hernandolopera.reservation_service.dto.UsuarioDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteAuth {

	private final RestTemplate restTemplate;

	@Value("${services.auth.url}")
	private String authUrl;

	@SuppressWarnings("unchecked")
	public Optional<UsuarioDTO> obtenerUsuarioPorId(Long idUsuario) {
		try {
			Map<String, Object> raw = restTemplate.getForObject(
					authUrl + "/api/internal/users/" + idUsuario, Map.class);
			if (raw == null) return Optional.empty();
			UsuarioDTO dto = UsuarioDTO.builder()
					.id(raw.get("id") != null ? ((Number) raw.get("id")).longValue() : null)
					.nombre((String) raw.get("firstName"))
					.apellido((String) raw.get("lastName"))
					.email((String) raw.get("email"))
					.telefono((String) raw.get("phone"))
					.rol((String) raw.get("role"))
					.activo(raw.get("active") instanceof Boolean ? (Boolean) raw.get("active") : null)
					.build();
			return Optional.of(dto);
		} catch (Exception e) {
			log.warn("No se pudo obtener datos del usuario {} desde auth-service: {}", idUsuario, e.getMessage());
			return Optional.empty();
		}
	}
}
