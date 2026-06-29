CREATE DATABASE IF NOT EXISTS reservation_db;
USE reservation_db;

CREATE TABLE IF NOT EXISTS reservation (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user BIGINT,
    package_id BIGINT,
    reservation_code VARCHAR(50) NOT NULL UNIQUE,
    passenger_count INT,
    total_amount DECIMAL(15,2) NOT NULL,
    travel_start_date DATETIME,
    travel_end_date DATETIME,
    status VARCHAR(255) NOT NULL,
    payment_verified TINYINT(1) NOT NULL DEFAULT 0,
    reservation_date DATE NOT NULL,
    expires_at DATETIME,
    updated_at DATETIME,
    confirmation_date DATETIME,
    notes VARCHAR(500),
    viaje_id BIGINT,
    paquete_nombre VARCHAR(150),
    destino VARCHAR(150),
    tipo_habitacion VARCHAR(50),
    solicitud_especial VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS traveler (
    id_traveler BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    gender VARCHAR(50),
    nationality VARCHAR(80),
    data_completed TINYINT(1) NOT NULL DEFAULT 1,
    documents_verified TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE IF NOT EXISTS acompanante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    tipo_documento VARCHAR(10) NOT NULL,
    documento VARCHAR(50),
    fecha_nacimiento DATE,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE IF NOT EXISTS archivos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_carga DATETIME(6) NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(255) NOT NULL,
    tamanio_archivo BIGINT,
    tipo_archivo VARCHAR(255) NOT NULL,
    tipo_documento VARCHAR(255) NOT NULL,
    verificado BIT(1) NOT NULL,
    viajero_id BIGINT,
    reserva_id INT NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reservation(id_reservation)
);

CREATE TABLE IF NOT EXISTS reservation_emergency_contact (
    id_contact BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    relationship VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE IF NOT EXISTS cancellation (
    id_cancellation INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('pendiente','aprobada','rechazada') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE IF NOT EXISTS voucher (
    id_voucher INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    file_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);
