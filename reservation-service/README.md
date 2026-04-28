# Reservation Service - Agencia de Viajes Hernando Lopera

## Descripción

Microservicio para la gestión de reservas de paquetes turísticos. Implementado con Spring Boot 3.1.5, JPA/Hibernate y MySQL, siguiendo una arquitectura de capas y con soporte para Docker.

## Características

- ✅ CRUD completo de reservas
- ✅ Validación de cupos disponibles
- ✅ Generación de códigos de confirmación únicos
- ✅ Auditoría de cambios
- ✅ Manejo integral de excepciones
- ✅ API REST documentada con Swagger/OpenAPI
- ✅ Integración con otros microservicios
- ✅ Dockerfile optimizado multi-etapa
- ✅ Soporte para Redis cache
- ✅ Logging estructurado
- ✅ Health checks y monitoreo

## Requisitos Previos

- Java 17 o superior
- Maven 3.9+
- Docker 20.10+
- Docker Compose 1.29+
- MySQL 8.0+ (para desarrollo local sin Docker)
- Redis 7+ (opcional, para cache)

## Estructura del Proyecto

```
reservation-service/
├── src/
│   ├── main/
│   │   ├── java/com/vhl/reservationservice/
│   │   │   ├── config/
│   │   │   │   ├── RestClientConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   └── SchedulingConfig.java
│   │   │   ├── controller/
│   │   │   │   └── ReservationController.java
│   │   │   ├── service/
│   │   │   │   ├── ReservationService.java
│   │   │   │   ├── ReservationServiceImpl.java
│   │   │   │   ├── AuditService.java
│   │   │   │   └── ReservationEvent.java
│   │   │   ├── repository/
│   │   │   │   ├── ReservationRepository.java
│   │   │   │   └── ReservationAuditRepository.java
│   │   │   ├── model/
│   │   │   │   ├── Reservation.java
│   │   │   │   └── ReservationAudit.java
│   │   │   ├── dto/
│   │   │   │   ├── ReservationRequestDTO.java
│   │   │   │   ├── ReservationResponseDTO.java
│   │   │   │   ├── ReservationDetailDTO.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── PackageNotFoundException.java
│   │   │   │   ├── InsufficientSpotsException.java
│   │   │   │   └── ReservationException.java
│   │   │   ├── client/
│   │   │   │   └── PackageServiceClient.java
│   │   │   ├── util/
│   │   │   │   └── ConfirmationCodeGenerator.java
│   │   │   └── ReservationServiceApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/vhl/reservationservice/
│           └── ReservationServiceApplicationTests.java
├── Dockerfile
├── .dockerignore
├── pom.xml
└── README.md
```

## Configuración y Ejecución

### 1. Desarrollo Local (sin Docker)

#### Paso 1: Configurar Base de Datos

```sql
-- Crear base de datos
CREATE DATABASE reservation_db;

-- Crear usuario
CREATE USER 'reservation_user'@'localhost' IDENTIFIED BY 'reservation_password';
GRANT ALL PRIVILEGES ON reservation_db.* TO 'reservation_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Paso 2: Compilar el Proyecto

```bash
cd reservation-service
mvn clean install
```

#### Paso 3: Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8082`

### 2. Con Docker Compose

#### Paso 1: Desde la Raíz del Proyecto

```bash
# Compilar todas las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f reservation-service

# Detener servicios
docker-compose down
```

#### Paso 2: Verificar Servicios

```bash
# Verificar estado de los contenedores
docker-compose ps

# Verificar health del servicio
curl http://localhost:8082/api/reservations/health
```

### 3. Solo Reservation Service

```bash
# Compilar imagen
docker build -t reservation-service:1.0 .

# Ejecutar contenedor
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/reservation_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -p 8082:8082 \
  --name reservation-service \
  reservation-service:1.0

# Ver logs
docker logs -f reservation-service

# Detener
docker stop reservation-service
docker rm reservation-service
```

## API Endpoints

### Base URL: `http://localhost:8082/api/reservations`

#### 1. Crear Reserva
```http
POST /api/reservations
Content-Type: application/json

{
  "packageId": 1,
  "userId": 1,
  "numberOfSpots": 2,
  "notes": "Preferencia de habitación doble"
}

Response: 201 Created
{
  "success": true,
  "message": "Reserva creada exitosamente",
  "data": {
    "id": 1,
    "packageId": 1,
    "userId": 1,
    "numberOfSpots": 2,
    "totalPrice": 1000.00,
    "status": "PENDING",
    "confirmationCode": "RES-ABC123-2026",
    "createdAt": "2026-04-28T10:30:00"
  }
}
```

#### 2. Obtener Reserva por ID
```http
GET /api/reservations/1

Response: 200 OK
{
  "success": true,
  "message": "Reserva encontrada",
  "data": { ... }
}
```

#### 3. Obtener Todas las Reservas
```http
GET /api/reservations

Response: 200 OK
{
  "success": true,
  "message": "Reservas obtenidas exitosamente",
  "data": [...]
}
```

#### 4. Obtener Reservas por Usuario
```http
GET /api/reservations/user/1
```

#### 5. Obtener Reservas por Paquete
```http
GET /api/reservations/package/1
```

#### 6. Actualizar Reserva
```http
PUT /api/reservations/1
Content-Type: application/json

{
  "packageId": 1,
  "userId": 1,
  "numberOfSpots": 3,
  "notes": "Cambio a 3 personas"
}

Response: 200 OK
```

#### 7. Confirmar Reserva
```http
PUT /api/reservations/1/confirm

Response: 200 OK
{
  "success": true,
  "message": "Reserva confirmada exitosamente",
  "code": "RES_CONFIRMED"
}
```

#### 8. Cancelar Reserva
```http
DELETE /api/reservations/1

Response: 200 OK
{
  "success": true,
  "message": "Reserva cancelada exitosamente",
  "code": "RES_CANCELLED"
}
```

#### 9. Health Check
```http
GET /api/reservations/health

Response: 200 OK
{
  "success": true,
  "message": "Servicio en línea",
  "data": "OK"
}
```

## Documentación Swagger

Una vez que la aplicación esté ejecutándose, accede a la documentación interactiva en:

```
http://localhost:8082/swagger-ui.html
```

O la especificación OpenAPI en:

```
http://localhost:8082/v3/api-docs
```

## Monitoreo y Actuator

### Endpoints de Actuator

- Health: `http://localhost:8082/actuator/health`
- Métricas: `http://localhost:8082/actuator/metrics`
- Info: `http://localhost:8082/actuator/info`
- Prometheus: `http://localhost:8082/actuator/prometheus`

## Códigos de Error

| Código | HTTP Status | Descripción |
|--------|------------|-------------|
| PACKAGE_NOT_FOUND | 404 | Paquete o reserva no encontrada |
| INSUFFICIENT_SPOTS | 400 | Cupos insuficientes |
| VALIDATION_ERROR | 400 | Error en validación de datos |
| RESERVATION_ERROR | 400 | Error genérico de reserva |
| INTERNAL_ERROR | 500 | Error interno del servidor |

## Validaciones

### Campos Requeridos

- `packageId`: Requerido, debe ser > 0
- `userId`: Requerido, debe ser > 0
- `numberOfSpots`: Requerido, debe ser > 0 y <= 10

### Reglas de Negocio

1. No se pueden crear reservas sin cupos disponibles
2. Máximo 10 cupos por reserva
3. Solo se pueden actualizar reservas en estado PENDING
4. El código de confirmación es único y se genera automáticamente
5. El precio total se calcula como: precio del paquete × número de cupos

## Variables de Ambiente

```properties
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/reservation_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Servicios Externos
SERVICE_PACKAGE_URL=http://localhost:8081
SERVICE_AUTH_URL=http://localhost:8080

# Configuración de Reserva
RESERVATION_MAX_SPOTS_PER_RESERVATION=10
RESERVATION_CACHE_ENABLED=true
RESERVATION_CACHE_TTL=3600
```

## Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=ReservationServiceApplicationTests

# Con cobertura
mvn test jacoco:report
```

## Logging

Los logs se guardan en: `logs/reservation-service.log`

Niveles de log:
- **DEBUG**: Información detallada (localhost)
- **INFO**: Operaciones normales
- **WARN**: Advertencias
- **ERROR**: Errores

## Performance y Optimizaciones

1. **Índices en BD**: `user_id`, `package_id`, `confirmation_code`, `status`
2. **Connection Pool**: HikariCP con máx 10 conexiones
3. **JPA Batch**: Batch size de 20 para mejor rendimiento
4. **Versionado**: Control optimista con @Version
5. **DTO Mapping**: Conversión automática entre entities y DTOs

## Seguridad

- ✅ Validación de entrada en todos los endpoints
- ✅ CORS configurado
- ✅ Manejo seguro de excepciones (sin stack traces en producción)
- ✅ Auditoría de cambios
- ✅ Sanitización de datos

## Troubleshooting

### Puerto 8082 en uso
```bash
# Encontrar proceso usando puerto
lsof -i :8082

# O cambiar en application.yml
server.port: 8083
```

### Error de conexión a BD
```bash
# Verificar que MySQL está corriendo
docker ps | grep mysql

# Verificar credenciales en application.yml
```

### Error en Docker Compose
```bash
# Ver logs completos
docker-compose logs

# Reconstruir imágenes
docker-compose build --no-cache

# Limpiar todo
docker-compose down -v
```

## Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Pull Request

## Roadmap Futuro

- [ ] Implementar caché con Redis
- [ ] Eventos asíncrónos con Kafka
- [ ] Notificaciones por email
- [ ] Reportes de reservas
- [ ] Dashboard de administración
- [ ] Integración con pasarelas de pago
- [ ] Sistema de reseñas y calificaciones

## Licencia

Este proyecto es parte de la "Agencia de Viajes y Excursiones Hernando Lopera"

## Contacto

Para soporte o consultas, contactar a: info@vhl.com

---

**Última actualización**: 28 de Abril de 2026  
**Versión**: 1.0.0  
**Estado**: En Desarrollo
