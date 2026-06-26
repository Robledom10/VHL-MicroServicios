CREATE DATABASE IF NOT EXISTS pago_db;

USE pago_db;
-- =========================================
-- TABLA: accounts_receivable
-- Representa la deuda general de una reserva
-- =========================================

CREATE TABLE accounts_receivable (
    id_account INT AUTO_INCREMENT PRIMARY KEY,
    id_reservation INT NOT NULL,
    total_debt DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    pending_amount DECIMAL(10, 2) NOT NULL,
    installments INT NOT NULL,
    status ENUM(
        'pendiente',
        'parcial',
        'pagado'
    ) NOT NULL DEFAULT 'pendiente',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- TABLA: payment
-- Representa cada movimiento financiero
-- (cuotas, pagos parciales, abonos, etc.)
-- =========================================

CREATE TABLE payment (
    id_payment INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_account INT NOT NULL,
    payment_method ENUM(
        'tarjeta',
        'transferencia',
        'paypal',
        'efectivo',
        'otro'
    ) NOT NULL,
    installment_number INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM(
        'pendiente',
        'aprobado',
        'rechazado',
        'reembolsado'
    ) NOT NULL DEFAULT 'pendiente',
    reference_number VARCHAR(100) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- TABLA: invoice
-- Facturas generadas por pagos
-- =========================================

CREATE TABLE invoice (
    id_invoice INT AUTO_INCREMENT PRIMARY KEY,
    fk_id_payment INT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    invoice_type ENUM('parcial', 'final') NOT NULL DEFAULT 'parcial',
    total_amount DECIMAL(10, 2) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    issue_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_id_payment) REFERENCES payment (id_payment)
);