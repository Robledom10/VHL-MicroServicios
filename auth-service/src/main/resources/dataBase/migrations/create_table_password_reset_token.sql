CREATE TABLE
    password_reset_token (
        id INT AUTO_INCREMENT PRIMARY KEY,
        token VARCHAR(255) NOT NULL UNIQUE,
        fk_id_user INT NOT NULL,
        expiry_date DATETIME NOT NULL,
        used TINYINT(1) NOT NULL DEFAULT 0,
        FOREIGN KEY (fk_id_user) REFERENCES `user` (id_user) ON DELETE CASCADE
    );
