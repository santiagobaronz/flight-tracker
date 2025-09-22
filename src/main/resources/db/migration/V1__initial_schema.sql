-- V1: Initial baseline schema

-- Academies
CREATE TABLE IF NOT EXISTS academies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    address VARCHAR(255),
    phone_number VARCHAR(50),
    logo VARCHAR(255),
    icon VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    academy_id BIGINT NOT NULL REFERENCES academies(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- User-Role join table for ManyToMany
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT user_roles_pk PRIMARY KEY (user_id, role_id)
);

-- Role Permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    module_code VARCHAR(40) NOT NULL,
    action VARCHAR(20) NOT NULL,
    CONSTRAINT role_permissions_uniq UNIQUE (role_id, module_code, action)
);

-- Aircrafts
CREATE TABLE IF NOT EXISTS aircrafts (
    id BIGSERIAL PRIMARY KEY,
    academy_id BIGINT NOT NULL REFERENCES academies(id) ON DELETE CASCADE,
    registration VARCHAR(20) NOT NULL,
    model VARCHAR(80) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT aircrafts_academy_registration_uniq UNIQUE (academy_id, registration)
);

-- Academy Modules
CREATE TABLE IF NOT EXISTS academy_modules (
    id BIGSERIAL PRIMARY KEY,
    academy_id BIGINT NOT NULL REFERENCES academies(id) ON DELETE CASCADE,
    section VARCHAR(20) NOT NULL DEFAULT 'APPLICATION',
    module_code VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    name VARCHAR(120),
    description VARCHAR(500),
    route VARCHAR(255),
    CONSTRAINT academy_modules_academy_module_uniq UNIQUE (academy_id, module_code)
);

-- Hour Purchases
CREATE TABLE IF NOT EXISTS hour_purchases (
    id BIGSERIAL PRIMARY KEY,
    academy_id BIGINT NOT NULL REFERENCES academies(id) ON DELETE CASCADE,
    client_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    aircraft_id BIGINT NOT NULL REFERENCES aircrafts(id) ON DELETE RESTRICT,
    receipt_number VARCHAR(60) NOT NULL,
    hours DOUBLE PRECISION NOT NULL,
    purchase_date DATE NOT NULL,
    created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT hour_purchases_receipt_aircraft_uniq UNIQUE (receipt_number, aircraft_id)
);

-- Hour Usages
CREATE TABLE IF NOT EXISTS hour_usages (
    id BIGSERIAL PRIMARY KEY,
    academy_id BIGINT NOT NULL REFERENCES academies(id) ON DELETE CASCADE,
    client_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    aircraft_id BIGINT NOT NULL REFERENCES aircrafts(id) ON DELETE RESTRICT,
    instructor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    hours DOUBLE PRECISION NOT NULL,
    flight_date DATE NOT NULL,
    logbook_number VARCHAR(60) NOT NULL,
    created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT
);

-- User Aircraft Balances
CREATE TABLE IF NOT EXISTS user_aircraft_balances (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    aircraft_id BIGINT NOT NULL REFERENCES aircrafts(id) ON DELETE CASCADE,
    total_purchased DOUBLE PRECISION NOT NULL,
    total_used DOUBLE PRECISION NOT NULL,
    balance_hours DOUBLE PRECISION NOT NULL,
    CONSTRAINT user_aircraft_balances_client_aircraft_uniq UNIQUE (client_id, aircraft_id)
);
