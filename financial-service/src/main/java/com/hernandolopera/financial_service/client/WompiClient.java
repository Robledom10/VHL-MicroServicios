package com.hernandolopera.financial_service.client;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.hernandolopera.financial_service.config.WompiProperties;
import com.hernandolopera.financial_service.dto.request.WompiRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WompiClient {

    private final WebClient webClient;
    private final WompiProperties properties;

    public Mono<Map> createTransaction(WompiRequest request) {
        return webClient.post()
                .uri("/payment_links")
                .header("Authorization", "Bearer " + properties.getPrivateKey())
                .bodyValue(request)
                .retrieve()
                // 🔍 Interceptamos el código 422 de Wompi
                .onStatus(status -> status.value() == 422, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            // Imprime el JSON exacto con los errores de Wompi en tus logs de Docker
                            System.err.println("[Wompi API Error 422]: " + errorBody);

                            // Propagamos un error controlado con la respuesta real de Wompi
                            return Mono.error(new WebClientResponseException(
                                    422,
                                    "Unprocessable Content",
                                    clientResponse.headers().asHttpHeaders(),
                                    errorBody.getBytes(),
                                    null));
                        }))
                .bodyToMono(Map.class);
    }
}