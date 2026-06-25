package com.hernandolopera.operation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OperationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OperationServiceApplication.class, args);
    }
}
