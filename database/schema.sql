-- Optional: the app creates these tables automatically (ddl-auto=update). Use this for reference.
CREATE DATABASE IF NOT EXISTS cleantrack_db;
USE cleantrack_db;
CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL, role ENUM('USER','ADMIN','COLLECTOR') NOT NULL);
CREATE TABLE complaint (id BIGINT AUTO_INCREMENT PRIMARY KEY, description VARCHAR(1000) NOT NULL, location VARCHAR(255) NOT NULL,
  status ENUM('PENDING','ASSIGNED','IN_PROGRESS','RESOLVED') NOT NULL, created_at DATETIME(6),
  user_id BIGINT NOT NULL, collector_id BIGINT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id), FOREIGN KEY (collector_id) REFERENCES users(id));
CREATE TABLE feedback (id BIGINT AUTO_INCREMENT PRIMARY KEY, complaint_id BIGINT NOT NULL UNIQUE, rating INT NOT NULL,
  comment VARCHAR(500), created_at DATETIME(6), FOREIGN KEY (complaint_id) REFERENCES complaint(id));
-- First admin (admin@cleantrack.com / Admin@123) is created automatically by the application on first start.
