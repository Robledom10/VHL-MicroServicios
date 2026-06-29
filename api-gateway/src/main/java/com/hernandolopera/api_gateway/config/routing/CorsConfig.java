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

        // allowedOriginPatterns is required when allowCredentials=true (wildcard origins not supported)
        config.addAllowedOriginPattern("http://localhost:4200");
        config.addAllowedOriginPattern("https://hernando-lopera-jade.vercel.app");
        config.addAllowedOriginPattern("https://*.ngrok-free.app");
        config.addAllowedOriginPattern("https://*.ngrok-free.dev");

        config.addAllowedHeader("*");

        config.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        config.addExposedHeader("Content-Disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
