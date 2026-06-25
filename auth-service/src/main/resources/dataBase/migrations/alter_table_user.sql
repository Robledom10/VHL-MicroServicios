use auth_service;

-- Cambiar document_type de ENUM a VARCHAR para que JPA pueda guardar el nombre del enum Java
ALTER TABLE `user` MODIFY COLUMN `document_type` VARCHAR(50) NULL;

-- Insertar roles requeridos por el código (CLIENT, ADMIN, GUIDE)
INSERT IGNORE INTO role (name, status) VALUES ('CLIENT', 1);
INSERT IGNORE INTO role (name, status) VALUES ('ADMIN', 1);
INSERT IGNORE INTO role (name, status) VALUES ('GUIDE', 1);

SELECT * FROM `role`;