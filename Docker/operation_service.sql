CREATE DATABASE IF NOT EXISTS trip_operations_db;
USE trip_operations_db;

CREATE TABLE IF NOT EXISTS trip_departure (
    id_trip BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user BIGINT NOT NULL,
    id_package BIGINT NOT NULL,
    departure_date DATE NOT NULL,
    return_date DATE NOT NULL,
    status ENUM('programado','en_curso','finalizado','cancelado') NOT NULL DEFAULT 'programado'
);

CREATE TABLE IF NOT EXISTS transport_assignment (
    id_transport BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    company VARCHAR(100) NOT NULL,
    vehicle_type VARCHAR(100) NOT NULL,
    plate VARCHAR(20) NOT NULL,
    driver_name VARCHAR(120),
    driver_phone VARCHAR(30),
    capacity INT NOT NULL DEFAULT 1,
    traveler_count INT NOT NULL DEFAULT 1,
    departure_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS lodging_assignment (
    id_lodging BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    id_traveler BIGINT,
    traveler_name VARCHAR(150),
    hotel_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    room_number VARCHAR(60),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS trip_incident (
    id_incident BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    id_traveler BIGINT,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    severity VARCHAR(30),
    reported_by VARCHAR(120),
    incident_date DATETIME NOT NULL,
    status ENUM('pendiente','en_proceso','resuelto') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS traveler_check_in (
    id_check_in BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    id_traveler BIGINT NOT NULL,
    qr_code VARCHAR(180) NOT NULL,
    id_reservation BIGINT,
    check_in_date DATETIME NOT NULL,
    UNIQUE KEY uk_check_in_trip_traveler (fk_id_trip, id_traveler),
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS traveler_medical_info (
    id_medical_info BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    id_traveler BIGINT NOT NULL,
    blood_type VARCHAR(10) NOT NULL,
    allergies VARCHAR(500),
    medications VARCHAR(500),
    medical_conditions VARCHAR(500),
    medical_phone VARCHAR(30),
    traveler_name VARCHAR(150),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS emergency_contact (
    id_contact BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    id_traveler BIGINT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    relationship VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    traveler_name VARCHAR(150),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS notification_history (
    id_notification BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    subject VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    recipient_count INT NOT NULL,
    recipients TEXT,
    sent_at DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    message_ids LONGTEXT,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS guide_assignment (
    id_guide BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    guide_name VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(150),
    specialty VARCHAR(200),
    language VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS restaurant_assignment (
    id_restaurant BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip BIGINT NOT NULL,
    restaurant_name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(30),
    meal_type VARCHAR(100),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS email_respuestas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notificacion_id BIGINT NOT NULL,
    remitente_email VARCHAR(255),
    asunto VARCHAR(500),
    contenido MEDIUMTEXT,
    fecha_recibida DATETIME,
    leida TINYINT(1) NOT NULL DEFAULT 0,
    incoming_message_id VARCHAR(500) UNIQUE
);
