-- V8: Rename pilot -> client, seed roles and permissions per YAML, enable modules

-- 1) Rename columns pilot_id -> client_id where needed
DO $$
BEGIN
    IF to_regclass('public.hour_purchases') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
             WHERE table_schema = current_schema()
               AND table_name = 'hour_purchases'
               AND column_name = 'pilot_id'
        ) THEN
            EXECUTE 'ALTER TABLE public.hour_purchases RENAME COLUMN pilot_id TO client_id';
        END IF;
    END IF;

    IF to_regclass('public.hour_usages') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
             WHERE table_schema = current_schema()
               AND table_name = 'hour_usages'
               AND column_name = 'pilot_id'
        ) THEN
            EXECUTE 'ALTER TABLE public.hour_usages RENAME COLUMN pilot_id TO client_id';
        END IF;
    END IF;

    IF to_regclass('public.user_aircraft_balances') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
             WHERE table_schema = current_schema()
               AND table_name = 'user_aircraft_balances'
               AND column_name = 'pilot_id'
        ) THEN
            EXECUTE 'ALTER TABLE public.user_aircraft_balances RENAME COLUMN pilot_id TO client_id';
        END IF;
    END IF;
END $$;

-- 1b) Adjust unique constraint on user_aircraft_balances from (pilot_id, aircraft_id) -> (client_id, aircraft_id)
DO $$
DECLARE
    conname text;
BEGIN
    IF to_regclass('public.user_aircraft_balances') IS NOT NULL THEN
        SELECT c.conname INTO conname
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        JOIN pg_namespace n ON t.relnamespace = n.oid
        WHERE t.relname = 'user_aircraft_balances'
          AND n.nspname = current_schema()
          AND c.contype = 'u'
          AND (pg_get_constraintdef(c.oid) ILIKE '%(pilot_id, aircraft_id)%' OR pg_get_constraintdef(c.oid) ILIKE '%("pilot_id", "aircraft_id")%');

        IF conname IS NOT NULL THEN
            EXECUTE 'ALTER TABLE public.user_aircraft_balances DROP CONSTRAINT ' || quote_ident(conname);
        END IF;

        -- add new unique constraint if not already present
        IF NOT EXISTS (
          SELECT 1 FROM pg_constraint c
          JOIN pg_class t ON c.conrelid = t.oid
          JOIN pg_namespace n ON t.relnamespace = n.oid
          WHERE t.relname = 'user_aircraft_balances'
            AND n.nspname = current_schema()
            AND c.contype = 'u'
            AND pg_get_constraintdef(c.oid) ILIKE '%(client_id, aircraft_id)%'
        ) THEN
          EXECUTE 'ALTER TABLE public.user_aircraft_balances ADD CONSTRAINT user_aircraft_balances_client_aircraft_uniq UNIQUE (client_id, aircraft_id)';
        END IF;
    END IF;
END $$;

-- 2) Ensure module_code checks allow both HOURS and AIRCRAFT (defensive)
ALTER TABLE IF EXISTS academy_modules DROP CONSTRAINT IF EXISTS academy_modules_module_code_check;
ALTER TABLE IF EXISTS academy_modules ADD CONSTRAINT academy_modules_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT'));

ALTER TABLE IF EXISTS role_permissions DROP CONSTRAINT IF EXISTS role_permissions_module_code_check;
ALTER TABLE IF EXISTS role_permissions ADD CONSTRAINT role_permissions_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT'));

-- 3) Roles per YAML: ADMINISTRATOR, EMPLOYEE, INSTRUCTOR, CLIENT
INSERT INTO roles(name, description)
SELECT 'ADMINISTRATOR', 'Administrators'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMINISTRATOR');

INSERT INTO roles(name, description)
SELECT 'EMPLOYEE', 'Employees'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'EMPLOYEE');

INSERT INTO roles(name, description)
SELECT 'INSTRUCTOR', 'Instructors'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'INSTRUCTOR');

INSERT INTO roles(name, description)
SELECT 'CLIENT', 'Clients'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CLIENT');

-- 4) Enable modules by section for all academies (modules_enabled from YAML)
-- APPLICATION: HOURS, AIRCRAFT
INSERT INTO academy_modules (academy_id, section, module_code, active, name, description, route)
SELECT a.id, 'APPLICATION', 'HOURS', TRUE, 'Horas', 'Gestión de horas', '/app/hours'
FROM academies a
WHERE NOT EXISTS (
  SELECT 1 FROM academy_modules m WHERE m.academy_id = a.id AND m.module_code = 'HOURS'
);

INSERT INTO academy_modules (academy_id, section, module_code, active, name, description, route)
SELECT a.id, 'APPLICATION', 'AIRCRAFT', TRUE, 'Aeronaves', 'Listado de aeronaves', '/app/aircraft'
FROM academies a
WHERE NOT EXISTS (
  SELECT 1 FROM academy_modules m WHERE m.academy_id = a.id AND m.module_code = 'AIRCRAFT'
);

-- MANAGEMENT: AIRCRAFT
INSERT INTO academy_modules (academy_id, section, module_code, active, name, description, route)
SELECT a.id, 'MANAGEMENT', 'AIRCRAFT', TRUE, 'Aeronaves (Admin)', 'Gestión de aeronaves', '/mgmt/aircraft'
FROM academies a
WHERE NOT EXISTS (
  SELECT 1 FROM academy_modules m WHERE m.academy_id = a.id AND m.section = 'MANAGEMENT' AND m.module_code = 'AIRCRAFT'
);

-- 5) Role permissions per YAML (note: section is not stored; use union of actions across sections)
-- ADMINISTRATOR: HOURS = [VIEW, CREATE, EDIT, DELETE]; AIRCRAFT = [VIEW, CREATE, EDIT, DELETE]
WITH r AS (
  SELECT id FROM roles WHERE name = 'ADMINISTRATOR'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES
  ('HOURS','VIEW'),('HOURS','CREATE'),('HOURS','EDIT'),('HOURS','DELETE'),
  ('AIRCRAFT','VIEW'),('AIRCRAFT','CREATE'),('AIRCRAFT','EDIT'),('AIRCRAFT','DELETE')
) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.module_code = x.module_code AND rp.action = x.action
);

-- EMPLOYEE: HOURS = [VIEW, CREATE, EDIT]; AIRCRAFT = [VIEW]
WITH r AS (
  SELECT id FROM roles WHERE name = 'EMPLOYEE'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES
  ('HOURS','VIEW'),('HOURS','CREATE'),('HOURS','EDIT'),
  ('AIRCRAFT','VIEW')
) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.module_code = x.module_code AND rp.action = x.action
);

-- INSTRUCTOR: HOURS = [VIEW, CREATE, EDIT]
WITH r AS (
  SELECT id FROM roles WHERE name = 'INSTRUCTOR'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES
  ('HOURS','VIEW'),('HOURS','CREATE'),('HOURS','EDIT')
) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.module_code = x.module_code AND rp.action = x.action
);

-- CLIENT: HOURS = [VIEW]
WITH r AS (
  SELECT id FROM roles WHERE name = 'CLIENT'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES
  ('HOURS','VIEW')
) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.module_code = x.module_code AND rp.action = x.action
);
