-- Lost & Found Management System (DBMS Mini Project)
-- MySQL Schema + Seed Data
-- Source of truth: workflow.md (May 2026)
--
-- Notes (beginner-friendly):
-- 1) We use soft delete for items via `is_deleted`.
-- 2) Status columns use ENUM to restrict valid values.
-- 3) Foreign keys enforce referential integrity.

-- Create and select database
CREATE DATABASE IF NOT EXISTS findora_db;
USE findora_db;

-- =========================
-- 1) users
-- =========================
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 2) categories
-- =========================
CREATE TABLE categories (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
);

-- =========================
-- 3) items
-- =========================
CREATE TABLE items (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    category_id   INT NOT NULL,
    color         VARCHAR(50),
    keywords      VARCHAR(500),
    status        ENUM('LOST', 'FOUND', 'CLAIMED') NOT NULL,
    location      VARCHAR(200),
    date_reported DATE NOT NULL,
    reporter_id   INT NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_items_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_items_reporter
        FOREIGN KEY (reporter_id)
        REFERENCES users(id)
);

-- =========================
-- 4) claims
-- =========================
CREATE TABLE claims (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    item_id      INT NOT NULL,
    claimant_id  INT NOT NULL,
    proof_text   TEXT,
    status       ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_claims_item
        FOREIGN KEY (item_id)
        REFERENCES items(id),

    CONSTRAINT fk_claims_claimant
        FOREIGN KEY (claimant_id)
        REFERENCES users(id)
);

-- =========================
-- 5) audit_logs
-- =========================
CREATE TABLE audit_logs (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT,
    action       VARCHAR(100) NOT NULL,
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    INT,
    timestamp    DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- =========================
-- Indexes (as suggested in workflow.md)
-- =========================
-- Items
CREATE INDEX idx_items_status ON items(status);
CREATE INDEX idx_items_is_deleted ON items(is_deleted);
CREATE INDEX idx_items_reporter ON items(reporter_id);
CREATE INDEX idx_items_category ON items(category_id);
CREATE INDEX idx_items_date ON items(date_reported);
CREATE INDEX idx_items_status_deleted ON items(status, is_deleted);

-- Claims
CREATE INDEX idx_claims_item ON claims(item_id);
CREATE INDEX idx_claims_claimant ON claims(claimant_id);

-- Audit logs
CREATE INDEX idx_audit_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);

-- =========================
-- Seed Data (sample data from workflow.md)
-- =========================

-- Seed categories
INSERT INTO categories (name) VALUES
('Electronics'), ('Clothing'), ('Books'), ('Bags'), ('Keys'),
('Wallets'), ('Accessories'), ('Sports Equipment'), ('Documents'), ('Other');

-- Seed users
-- NOTE: For this academic project, workflow.md mentions passwords may be plain text.
-- If you later add hashing in the backend, adjust these values accordingly.
INSERT INTO users (name, email, password) VALUES
('Alice Sharma', 'alice@college.edu', 'hashed_password_1'),
('Bob Kumar',   'bob@college.edu',   'hashed_password_2');

-- Seed items
INSERT INTO items (
    title, description, category_id, color, keywords,
    status, location, date_reported, reporter_id
) VALUES
(
    'Blue Water Bottle',
    'Stainless steel bottle with college logo',
    7,
    'blue',
    'bottle,steel,water',
    'LOST',
    'Library Block A',
    '2024-06-01',
    1
),
(
    'Found Keys Bundle',
    'Set of 3 keys on a red keychain',
    5,
    'red',
    'keys,keychain,red',
    'FOUND',
    'Canteen',
    '2024-06-02',
    2
);
