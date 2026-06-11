CREATE DATABASE IF NOT EXISTS catalogo_turistico_db;
USE catalogo_turistico_db;

CREATE TABLE `package` (
    id_package INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    destination VARCHAR(150) NOT NULL,
    duration_days INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    quota INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    departure_place VARCHAR(150),
    transport_type VARCHAR(80) DEFAULT 'Bus de turismo',
    vertical_photo_url VARCHAR(500),
    horizontal_photo_url VARCHAR(500),
    status TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE itinerarie (
    id_itinerary INT AUTO_INCREMENT PRIMARY KEY,
    day_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    fk_id_package INT NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_destination (
    fk_id_package INT NOT NULL,
    destination VARCHAR(150) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_transport_type (
    fk_id_package INT NOT NULL,
    transport_type VARCHAR(80) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE provider (
    id_provider INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(150),
    status TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE package_provider (
    id_package_provider INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_package INT NOT NULL,
    fk_id_provider INT NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package),
    FOREIGN KEY (fk_id_provider) REFERENCES provider(id_provider)
);

CREATE TABLE package_inclusion (
    fk_id_package INT NOT NULL,
    description VARCHAR(150) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_exclusion (
    fk_id_package INT NOT NULL,
    description VARCHAR(150) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_cancellation_policy (
    fk_id_package INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_requirement (
    fk_id_package INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);

CREATE TABLE package_comment (
    id_comment INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_package INT NOT NULL,
    comment VARCHAR(500) NOT NULL,
    score INT NOT NULL,
    author VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (score BETWEEN 1 AND 5),
    FOREIGN KEY (fk_id_package) REFERENCES `package`(id_package)
);
