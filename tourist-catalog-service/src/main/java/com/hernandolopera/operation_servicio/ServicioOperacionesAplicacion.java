package com.hernandolopera.operation_servicio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.hernandolopera.operation_servicio.entidades")
@EnableJpaRepositories("com.hernandolopera.operation_servicio.repositorio")
public class ServicioOperacionesAplicacion {
    public static void main(String[] args) {
        SpringApplication.run(ServicioOperacionesAplicacion.class, args);
    }
}
