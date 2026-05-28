CREATE DATABASE IF NOT EXISTS trip_operations_db;
USE trip_operations_db;

CREATE TABLE IF NOT EXISTS trip_departure (
    id_trip INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_package INT NOT NULL,
    departure_date DATE NOT NULL,
    return_date DATE NOT NULL,
    status ENUM('programado','en_curso','finalizado','cancelado') NOT NULL DEFAULT 'programado'
);

CREATE TABLE IF NOT EXISTS transport_assignment (
    id_transport INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
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
    id_lodging INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    id_traveler INT,
    hotel_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    room_number VARCHAR(60),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS trip_incident (
    id_incident INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    id_traveler INT,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    severity VARCHAR(30),
    reported_by VARCHAR(120),
    incident_date DATETIME NOT NULL,
    status ENUM('pendiente','en_proceso','resuelto') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS traveler_check_in (
    id_check_in INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    id_traveler INT NOT NULL,
    qr_code VARCHAR(180) NOT NULL,
    id_reservation INT,
    check_in_date DATETIME NOT NULL,
    UNIQUE KEY uk_check_in_trip_traveler (fk_id_trip, id_traveler),
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS traveler_medical_info (
    id_medical_info INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    id_traveler INT NOT NULL,
    blood_type VARCHAR(10) NOT NULL,
    allergies VARCHAR(500),
    medications VARCHAR(500),
    medical_conditions VARCHAR(500),
    medical_phone VARCHAR(30),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS emergency_contact (
    id_contact INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    id_traveler INT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    relationship VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);

CREATE TABLE IF NOT EXISTS notification_history (
    id_notification INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_trip INT NOT NULL,
    subject VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    recipient_count INT NOT NULL,
    recipients TEXT,
    sent_at DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    FOREIGN KEY (fk_id_trip) REFERENCES trip_departure(id_trip)
);
