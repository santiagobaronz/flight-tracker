-- V3: Extra fields for AcademyModule and attributes table

-- 1) Add new optional columns to academy_modules
ALTER TABLE academy_modules ADD COLUMN IF NOT EXISTS name varchar(120);
ALTER TABLE academy_modules ADD COLUMN IF NOT EXISTS description varchar(500);
ALTER TABLE academy_modules ADD COLUMN IF NOT EXISTS route varchar(255);

-- 2) Create attributes collection table (for @ElementCollection List<String>)
CREATE TABLE IF NOT EXISTS academy_module_attributes (
    academy_module_id BIGINT NOT NULL REFERENCES academy_modules(id) ON DELETE CASCADE,
    attribute VARCHAR(100) NOT NULL
);

-- 3) Optional: index for faster lookups by module id
CREATE INDEX IF NOT EXISTS idx_academy_module_attributes_module_id ON academy_module_attributes(academy_module_id);
