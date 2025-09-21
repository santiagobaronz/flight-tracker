-- 1) Agregar columnas sin NOT NULL
ALTER TABLE academies ADD COLUMN IF NOT EXISTS address varchar(255);
ALTER TABLE academies ADD COLUMN IF NOT EXISTS phone_number varchar(50);
ALTER TABLE academies ADD COLUMN IF NOT EXISTS logo varchar(255);
ALTER TABLE academies ADD COLUMN IF NOT EXISTS icon varchar(255);
ALTER TABLE academies ADD COLUMN IF NOT EXISTS is_active boolean;

-- 2) Backfill de filas existentes
UPDATE academies SET address = 'Pending address' WHERE address IS NULL;
UPDATE academies SET phone_number = '+0000000000' WHERE phone_number IS NULL;
UPDATE academies SET is_active = TRUE WHERE is_active IS NULL;

-- 3) Marcar restricciones NOT NULL
ALTER TABLE academies ALTER COLUMN address SET NOT NULL;
ALTER TABLE academies ALTER COLUMN phone_number SET NOT NULL;
ALTER TABLE academies ALTER COLUMN is_active SET NOT NULL;

-- 4) (Opcional) Default para inserts futuros
ALTER TABLE academies ALTER COLUMN is_active SET DEFAULT TRUE;