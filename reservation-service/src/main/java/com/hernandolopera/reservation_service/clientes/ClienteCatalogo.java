package com.hernandolopera.reservation_service.clientes;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteCatalogo {

	private final RestTemplate restTemplate;

	@Value("${services.catalog.url}")
	private String catalogUrl;

	@SuppressWarnings("unchecked")
	private Map<String, Object> obtenerPaquete(Long idPaquete) {
		if (idPaquete == null) return null;
		try {
			return restTemplate.getForObject(catalogUrl + "/api/paquetes/" + idPaquete, Map.class);
		} catch (Exception e) {
			log.warn("No se pudo obtener paquete {}: {}", idPaquete, e.getMessage());
			return null;
		}
	}

	public String obtenerDestino(Long idPaquete) {
		Map<String, Object> raw = obtenerPaquete(idPaquete);
		return raw != null ? (String) raw.get("destino") : null;
	}

	public String obtenerNombre(Long idPaquete) {
		Map<String, Object> raw = obtenerPaquete(idPaquete);
		return raw != null ? (String) raw.get("titulo") : null;
	}
}
