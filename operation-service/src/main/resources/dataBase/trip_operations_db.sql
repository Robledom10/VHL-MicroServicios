CREATE DATABASE IF NOT EXISTS trip_operations_db;
USE trip_operations_db;

CREATE TABLE IF NOT EXISTS paquete_turistico (
    id_paquete INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    destino VARCHAR(120) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    base_precio DECIMAL(12,2) NOT NULL,
    cupo_total INT NOT NULL,
    cupo_disponible INT NOT NULL,
    activo_reservations INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVO','INACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS itinerario_actividad (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    numero_dia INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    hora_inicio TIME NULL,
    hora_fin TIME NULL,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete)
);

CREATE TABLE IF NOT EXISTS tourism_proveedor (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    proveedor_type VARCHAR(80) NOT NULL,
    contact_nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS package_proveedor (
    fk_id_paquete INT NOT NULL,
    fk_id_proveedor INT NOT NULL,
    PRIMARY KEY (fk_id_paquete, fk_id_proveedor),
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete),
    FOREIGN KEY (fk_id_proveedor) REFERENCES tourism_proveedor(id_proveedor)
);

CREATE TABLE IF NOT EXISTS precio_plan (
    id_precio_plan INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    cuotas INT NOT NULL,
    condiciones VARCHAR(500) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete)
);

CREATE TABLE IF NOT EXISTS seguro_coverage (
    id_seguro INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    detalle_cobertura VARCHAR(500) NOT NULL,
    monto_cobertura DECIMAL(12,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete)
);

CREATE TABLE IF NOT EXISTS capacity_historial (
    id_capacity_historial INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_paquete INT NOT NULL,
    cupo_anterior INT NOT NULL,
    cupo_nuevo INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_paquete) REFERENCES paquete_turistico(id_paquete)
);

CREATE TABLE IF NOT EXISTS organization_perfil (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    organization_nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    logo_base64 LONGTEXT NULL,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS salida_viaje (
    id_viaje INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_paquete INT NOT NULL,
    fecha_salida DATE NOT NULL,
    fecha_retorno DATE NOT NULL,
    status ENUM('programado','en_curso','finalizado','cancelado') NOT NULL DEFAULT 'programado'
);

CREATE TABLE IF NOT EXISTS asignacion_transporte (
    id_transporte INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    company VARCHAR(100) NOT NULL,
    tipo_vehiculo VARCHAR(100) NOT NULL,
    plate VARCHAR(20) NOT NULL,
    hora_salida DATETIME NOT NULL,
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje)
);

CREATE TABLE IF NOT EXISTS asignacion_hospedaje (
    id_hospedaje INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    hotel_nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida_hospedaje DATE NOT NULL,
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje)
);

CREATE TABLE IF NOT EXISTS incidente_viaje (
    id_incidente INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_viaje INT NOT NULL,
    type VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    fecha_incidente DATETIME NOT NULL,
    status ENUM('pendiente','en_proceso','resuelto') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_viaje) REFERENCES salida_viaje(id_viaje)
);
