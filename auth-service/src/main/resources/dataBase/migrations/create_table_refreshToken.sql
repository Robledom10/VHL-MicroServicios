CREATE TABLE
    refresh_token (
        id_refresh_token INT AUTO_INCREMENT PRIMARY KEY,
        token VARCHAR(500) NOT NULL UNIQUE,
        expiry_date DATETIME NOT NULL,
        fk_id_user INT NOT NULL UNIQUE,
        FOREIGN KEY (fk_id_user) REFERENCES `user` (id_user) ON DELETE CASCADE
    );