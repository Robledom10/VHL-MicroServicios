package com.hernandolopera.operation_service.config;

// CORS is handled exclusively by the API Gateway (CorsWebFilter in api-gateway).
// Microservices behind the gateway must not add their own CORS headers — doing so
// produces duplicate Access-Control-Allow-Origin values that browsers reject.
public class CorsConfig {
}
