-- Run as MySQL root in MySQL Command Line Client.
-- Existing donor and emergency request records are preserved.
CREATE DATABASE IF NOT EXISTS blood_donor_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blood_donor_db;
CREATE TABLE IF NOT EXISTS donors (
 donor_id INT AUTO_INCREMENT PRIMARY KEY,
 full_name VARCHAR(120) NOT NULL, age INT NOT NULL,
 blood_group VARCHAR(3) NOT NULL, phone VARCHAR(15) NOT NULL,
 city VARCHAR(100) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS emergency_requests (
 request_id INT AUTO_INCREMENT PRIMARY KEY,
 patient_name VARCHAR(120) NOT NULL, blood_group VARCHAR(3) NOT NULL,
 units_required INT NOT NULL, hospital_name VARCHAR(160) NOT NULL,
 city VARCHAR(100) NOT NULL, contact_number VARCHAR(15) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS admins (
 admin_id INT AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(80) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS donor_accounts (
 donor_id INT PRIMARY KEY, username VARCHAR(80) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(donor_id) REFERENCES donors(donor_id) ON DELETE CASCADE
);
-- Create app user using your own NEW strong password; replace placeholder before executing.
-- CREATE USER IF NOT EXISTS 'blood_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_NEW_SECRET';
-- ALTER USER 'blood_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_NEW_SECRET';
-- GRANT SELECT, INSERT, UPDATE ON blood_donor_db.donors TO 'blood_app'@'localhost';
-- GRANT SELECT, INSERT ON blood_donor_db.donor_accounts TO 'blood_app'@'localhost';
-- GRANT SELECT, INSERT ON blood_donor_db.emergency_requests TO 'blood_app'@'localhost';
-- GRANT UPDATE(status) ON blood_donor_db.emergency_requests TO 'blood_app'@'localhost';
-- GRANT SELECT ON blood_donor_db.admins TO 'blood_app'@'localhost';
