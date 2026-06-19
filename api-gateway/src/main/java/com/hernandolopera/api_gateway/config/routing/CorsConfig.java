package com.hernandolopera.api_gateway.config.routing;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
public class CorsConfig {

        @Bean
        public CorsWebFilter corsWebFilter() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowCredentials(true);
                config.setAllowedOrigins(
                                Arrays.asList("http://localhost:4200", "https://hernando-lopera-jade.vercel.app"));
                config.setAllowedHeaders(
                                Arrays.asList("*"));
                config.setAllowedMethods(
                                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return new CorsWebFilter(source);
        }
}