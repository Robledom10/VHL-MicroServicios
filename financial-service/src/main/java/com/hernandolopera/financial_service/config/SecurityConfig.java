package com.hernandolopera.financial_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ✅ WEBHOOK WOMPI PUBLICO
                        .requestMatchers(
                                "/api/webhook/**")
                        .permitAll()

                        // ✅ CREAR LINK
                        .requestMatchers(
                                "/api/payments/create-link")
                        .permitAll()

                        // 🔒 TODO LO DEMÁS
                        .anyRequest().authenticated());

        return http.build();
    }
}