DROP DATABASE IF EXISTS reservas_db;
CREATE DATABASE reservas_db;
USE reservas_db;

CREATE TABLE reservation (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    package_id INT NOT NULL,
    reservation_code VARCHAR(50) NOT NULL UNIQUE,
    reservation_date DATE NOT NULL,
    status ENUM('pendiente','confirmada','cancelada') NOT NULL DEFAULT 'pendiente',
    total_amount DECIMAL(10,2) NOT NULL
);

CREATE TABLE cancellation (
    id_cancellation INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('pendiente','aprobada','rechazada') NOT NULL DEFAULT 'pendiente',
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);

CREATE TABLE voucher (
    id_voucher INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_reservation INT NOT NULL,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    file_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (fk_id_reservation) REFERENCES reservation(id_reservation)
);
