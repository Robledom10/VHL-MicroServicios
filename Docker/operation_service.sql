CREATE DATABASE IF NOT EXISTS trip_operations_db;
USE trip_operations_db;

CREATE TABLE IF NOT EXISTS paquete_turistico (
    id_paquete INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    destino VARCHAR(120) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    precio_base DECIMAL(12,2) NOT NULL,
    cupo_total INT NOT NULL,
    cupo_disponible INT NOT NULL,
    reservas_activas INT NOT NULL DEFAULT 0,
    estado ENUM('ACTIVO','INACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (precio_base > 0),
    CHECK (cupo_total >= 1),
    CHECK (cupo_disponible >= 0),
    CHECK (reservas_activas >= 0),
    CHECK (cupo_disponible <= cupo_total)
);

CREATE TABLE IF NOT EXISTS actividad_itinerario (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    numero_dia INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    hora_inicio TIME NULL,
    hora_fin TIME NULL,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete) ON DELETE CASCADE,
    CHECK (numero_dia >= 1),
    CHECK (hora_fin IS NULL OR hora_inicio IS NULL OR hora_fin > hora_inicio)
);

CREATE TABLE IF NOT EXISTS plan_precio (
    id_plan_precio INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    cuotas INT NOT NULL,
    condiciones VARCHAR(500) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete),
    CHECK (precio > 0),
    CHECK (cuotas >= 1)
);

CREATE TABLE IF NOT EXISTS proveedor_turistico (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    tipo_proveedor VARCHAR(80) NOT NULL,
    nombre_contacto VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS seguro_cobertura (
    id_seguro INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    detalle_cobertura VARCHAR(500) NOT NULL,
    monto_cobertura DECIMAL(12,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete),
    CHECK (monto_cobertura > 0)
);

CREATE TABLE IF NOT EXISTS historial_cupo (
    id_historial_cupo INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    cupo_anterior INT NOT NULL,
    cupo_nuevo INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete),
    CHECK (cupo_anterior >= 0),
    CHECK (cupo_nuevo >= 1)
);

CREATE TABLE IF NOT EXISTS perfil_organizacion (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    nombre_organizacion VARCHAR(150) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    logo_base64 LONGTEXT NULL,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS salida_viaje (
    id_viaje INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_paquete INT NOT NULL,
    fecha_salida DATE NOT NULL,
    fecha_retorno DATE NOT NULL,
    estado ENUM('programado','en_curso','finalizado','cancelado') NOT NULL DEFAULT 'programado',
    FOREIGN KEY (id_paquete) REFERENCES paquete_turistico(id_paquete),
    CHECK (fecha_retorno >= fecha_salida)
);

CREATE TABLE IF NOT EXISTS asignacion_transporte (
    id_transporte INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    empresa VARCHAR(100) NOT NULL,
    tipo_vehiculo VARCHAR(100) NOT NULL,
    placa VARCHAR(20) NOT NULL,
    hora_salida DATETIME NOT NULL,
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS asignacion_hospedaje (
    id_hospedaje INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    nombre_hotel VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida_hospedaje DATE NOT NULL,
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje) ON DELETE CASCADE,
    CHECK (fecha_salida_hospedaje >= fecha_entrada)
);

CREATE TABLE IF NOT EXISTS incidente_viaje (
    id_incidente INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    fecha_incidente DATETIME NOT NULL,
    estado ENUM('pendiente','en_proceso','resuelto') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje) ON DELETE CASCADE
);
