DROP DATABASE IF EXISTS reservation_db;

CREATE DATABASE reservation_db;

USE reservation_db;

CREATE TABLE reservation (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    package_id INT NOT NULL,
    reservation_code VARCHAR(50) NOT NULL UNIQUE,
    passenger_count INT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    travel_start_date DATETIME NULL,
    travel_end_date DATETIME NULL,
    status ENUM(
        'bloqueada',
        'pendiente',
        'confirmada',
        'cancelada',
        'completada'
    ) NOT NULL DEFAULT 'pendiente',
    payment_verified BOOLEAN NOT NULL DEFAULT FALSE,
    reservation_date DATE NOT NULL,
    expires_at DATETIME NULL,
    updated_at DATETIME NULL,
    confirmation_date DATETIME NULL,
    notes VARCHAR(500) NULL
);

CREATE TABLE traveler (
    id_traveler INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NULL,
    gender VARCHAR(50) NULL,
    nationality VARCHAR(80) NULL,
    data_completed BOOLEAN NOT NULL DEFAULT TRUE,
    documents_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation (id_reservation)
);

CREATE TABLE cancellation (
    id_cancellation INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    refund_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status ENUM(
        'pendiente',
        'aprobada',
        'rechazada'
    ) NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation (id_reservation)
);

CREATE TABLE voucher (
    id_voucher INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    file_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation (id_reservation)
);