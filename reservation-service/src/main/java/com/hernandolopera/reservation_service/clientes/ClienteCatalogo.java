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
	public String obtenerDestino(Long idPaquete) {
		if (idPaquete == null) return null;
		try {
			Map<String, Object> raw = restTemplate.getForObject(
					catalogUrl + "/api/paquetes/" + idPaquete, Map.class);
			if (raw == null) return null;
			return (String) raw.get("destino");
		} catch (Exception e) {
			log.warn("No se pudo obtener destino del paquete {}: {}", idPaquete, e.getMessage());
			return null;
		}
	}
}
