package com.hernandolopera.reservation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServicioReservaAplicacion {

	public static void main(String[] args) {
		SpringApplication.run(ServicioReservaAplicacion.class, args);
	}

}
