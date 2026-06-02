use auth_service;

ALTER TABLE `user` MODIFY COLUMN `document_type` ENUM (
    'Cedula Ciudadania',
    'Tarjeta Identidad',
    'Pasaporte',
    'Cedula Extranjeria',
    'Visa'
) NOT NULL;

INSERT INTO
    role (name, status)
VALUES
    ('USER', 1);

INSERT INTO
    role (name, status)
VALUES
    ('ADMIN', 1)
select
    *
from
    `role`