package com.hernandolopera.operation_service.servicios;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServicioWhatsApp {

    @Value("${whatsapp.phone-number-id:}")
    private String phoneNumberId;

    @Value("${whatsapp.access-token:}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviar(String telefono, String mensaje) {
        try {
            String url = "https://graph.facebook.com/v21.0/" + phoneNumberId + "/messages";
            String mensajeEscapado = mensaje.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
            String body = String.format(
                "{\"messaging_product\":\"whatsapp\",\"to\":\"%s\",\"type\":\"text\",\"text\":{\"body\":\"%s\"}}",
                telefono.trim(), mensajeEscapado
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
            log.info("WhatsApp enviado a: {}", telefono);
        } catch (Exception e) {
            log.error("Error enviando WhatsApp a {}: {}", telefono, e.getMessage());
        }
    }

    public void enviarMasivo(List<String> telefonos, String mensaje) {
        if (phoneNumberId == null || phoneNumberId.isBlank() || accessToken == null || accessToken.isBlank()) {
            log.warn("WhatsApp no configurado (WHATSAPP_PHONE_NUMBER_ID o WHATSAPP_ACCESS_TOKEN vacíos). Mensajes no enviados.");
            return;
        }
        telefonos.stream()
            .map(String::trim)
            .filter(t -> !t.isEmpty())
            .forEach(tel -> enviar(tel, mensaje));
    }
}
