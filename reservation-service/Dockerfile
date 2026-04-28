# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -X

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar herramientas de depuración y utilidades
RUN apk add --no-cache curl jq

# Copiar JAR del builder
COPY --from=builder /build/target/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs

# Exponer puerto
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8082/api/reservations/health || exit 1

# Punto de entrada
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-jar", "app.jar"]
