-- V9: System Principal and Role Type

-- 1) Add role_type to roles
ALTER TABLE roles ADD COLUMN IF NOT EXISTS role_type VARCHAR(20) NOT NULL DEFAULT 'OPTIONAL';

-- 2) Mark mandatory base roles
UPDATE roles SET role_type = 'MANDATORY' WHERE name IN ('ADMINISTRATOR','EMPLOYEE','INSTRUCTOR','CLIENT');

-- 3) Create system_principal single-row table
CREATE TABLE IF NOT EXISTS system_principal (
    id BIGINT PRIMARY KEY CHECK (id = 1),
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id)
);
