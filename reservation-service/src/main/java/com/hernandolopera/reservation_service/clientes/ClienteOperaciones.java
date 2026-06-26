package com.hernandolopera.reservation_service.clientes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.hernandolopera.reservation_service.dto.ContactoEmergenciaDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteOperaciones {

	private final RestTemplate restTemplate;

	@Value("${services.operation.url}")
	private String operationUrl;

	public void enviarContactosEmergencia(Long idViaje, List<ContactoEmergenciaDTO> contactos) {
		if (idViaje == null || contactos == null || contactos.isEmpty()) return;

		String url = operationUrl + "/api/v1/operaciones/viajes/" + idViaje + "/contactos-emergencia";

		for (ContactoEmergenciaDTO c : contactos) {
			try {
				Map<String, Object> body = new HashMap<>();
				body.put("nombre", c.getNombre());
				body.put("parentesco", c.getParentesco());
				body.put("telefono", c.getTelefono());
				if (c.getCorreo() != null && !c.getCorreo().isBlank()) {
					body.put("correo", c.getCorreo());
				}
				body.put("nombreViajero", "");

				restTemplate.postForObject(url, body, Map.class);
				log.info("Contacto de emergencia '{}' sincronizado con operation-service para viaje {}", c.getNombre(), idViaje);
			} catch (Exception e) {
				log.error("No se pudo sincronizar contacto '{}' con operation-service (viaje {}): {} — body enviado: nombre={}, parentesco={}, telefono={}",
						c.getNombre(), idViaje, e.getMessage(), c.getNombre(), c.getParentesco(), c.getTelefono());
			}
		}
	}
}
