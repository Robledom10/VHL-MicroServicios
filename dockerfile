# ==========================================
# ETAPA 1: Compilación de todos los servicios
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copiar las carpetas de tus 7 microservicios al contenedor
COPY api-gateway/ ./api-gateway/
COPY auth-service/ ./auth-service/
COPY financial-service/ ./financial-service/
COPY gallery-service/ ./gallery-service/
COPY operation-service/ ./operation-service/
COPY reservation-service/ ./reservation-service/
COPY tourist-catalog-service/ ./tourist-catalog-service/

# 2. Compilar cada uno de los microservicios en archivos .jar
RUN cd api-gateway && mvn clean package -DskipTests
RUN cd auth-service && mvn clean package -DskipTests
RUN cd financial-service && mvn clean package -DskipTests
RUN cd gallery-service && mvn clean package -DskipTests
RUN cd operation-service && mvn clean package -DskipTests
RUN cd reservation-service && mvn clean package -DskipTests
RUN cd tourist-catalog-service && mvn clean package -DskipTests

# ==========================================
# ETAPA 2: Entorno de ejecución unificado
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 3. Copiar únicamente los archivos .jar compilados de la etapa anterior
COPY --from=build /app/api-gateway/target/*.jar api-gateway.jar
COPY --from=build /app/auth-service/target/*.jar auth-service.jar
COPY --from=build /app/financial-service/target/*.jar financial-service.jar
COPY --from=build /app/gallery-service/target/*.jar gallery-service.jar
COPY --from=build /app/operation-service/target/*.jar operation-service.jar
COPY --from=build /app/reservation-service/target/*.jar reservation-service.jar
COPY --from=build /app/tourist-catalog-service/target/*.jar tourist-catalog-service.jar

# 4. Ajuste Extremo de Memoria RAM (Obligatorio para servidores gratuitos)
# -XX:+UseSerialGC usa un recolector de basura de bajísimo consumo.
# -Xmx48m limita a cada microservicio de Java a consumir máximo 48MB de RAM.
ENV JAVA_OPTS="-XX:+UseSerialGC -Xss256k -Xms16m -Xmx48m"

# 5. Configurar el puerto dinámico de Render para el API Gateway (Único servicio expuesto)
ENV PORT=8080

# 6. Arrancar todos los servicios en paralelo (&) y dejar el último en primer plano
ENTRYPOINT ["sh", "-c", "\
java $JAVA_OPTS -Dserver.port=8080 -jar api-gateway.jar & \
java $JAVA_OPTS -jar auth-service.jar & \
java $JAVA_OPTS -jar financial-service.jar & \
java $JAVA_OPTS -jar gallery-service.jar & \
java $JAVA_OPTS -jar operation-service.jar & \
java $JAVA_OPTS -jar reservation-service.jar & \
java $JAVA_OPTS -jar tourist-catalog-service.jar \
"]
