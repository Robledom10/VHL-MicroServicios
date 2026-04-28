package com.vhl.reservationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reservation Service API")
                        .version("1.0.0")
                        .description("API REST para gestión de reservas de paquetes turísticos")
                        .contact(new Contact()
                                .name("Agencia de Viajes Hernando Lopera")
                                .email("info@vhl.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Server"),
                        new Server().url("http://api-gateway:8080").description("API Gateway")
                ));
    }
}
