-- V10: Add ROLES module, checks, created/updated by, and seed MECANICO

-- 1) Extend module_code checks to include 'ROLES'
ALTER TABLE IF EXISTS academy_modules DROP CONSTRAINT IF EXISTS academy_modules_module_code_check;
ALTER TABLE IF EXISTS academy_modules ADD CONSTRAINT academy_modules_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT','ROLES'));

ALTER TABLE IF EXISTS role_permissions DROP CONSTRAINT IF EXISTS role_permissions_module_code_check;
ALTER TABLE IF EXISTS role_permissions ADD CONSTRAINT role_permissions_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT','ROLES'));

-- 2) Add created_by / updated_by on roles (nullable)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='roles' AND column_name='created_by'
    ) THEN
        EXECUTE 'ALTER TABLE roles ADD COLUMN created_by BIGINT REFERENCES users(id)';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns WHERE table_name='roles' AND column_name='updated_by'
    ) THEN
        EXECUTE 'ALTER TABLE roles ADD COLUMN updated_by BIGINT REFERENCES users(id)';
    END IF;
END$$;

-- 3) Seed AcademyModule for ROLES in MANAGEMENT
INSERT INTO academy_modules (academy_id, section, module_code, active, name, description, route)
SELECT a.id, 'MANAGEMENT', 'ROLES', TRUE, 'Roles', 'Gestión de roles', '/mgmt/roles'
FROM academies a
WHERE NOT EXISTS (
  SELECT 1 FROM academy_modules m WHERE m.academy_id = a.id AND m.section='MANAGEMENT' AND m.module_code = 'ROLES'
);

-- 4) Seed role MECANICO (OPTIONAL)
INSERT INTO roles(name, description, role_type)
SELECT 'MECANICO', 'Mecánicos', 'OPTIONAL'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='MECANICO');

-- 5) Seed permissions for ROLES module: ADMINISTRATOR (CRUD), EMPLOYEE (VIEW)
WITH r AS (
  SELECT id FROM roles WHERE name = 'ADMINISTRATOR'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES ('ROLES','VIEW'),('ROLES','CREATE'),('ROLES','EDIT'),('ROLES','DELETE')) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id=r.id AND rp.module_code=x.module_code AND rp.action=x.action
);

WITH r AS (
  SELECT id FROM roles WHERE name = 'EMPLOYEE'
)
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, x.module_code, x.action FROM r
CROSS JOIN (VALUES ('ROLES','VIEW')) AS x(module_code, action)
WHERE NOT EXISTS (
  SELECT 1 FROM role_permissions rp WHERE rp.role_id=r.id AND rp.module_code=x.module_code AND rp.action=x.action
);

-- 6) Seed MECANICO permissions equal to INSTRUCTOR in ALL modules (current: HOURS [VIEW, CREATE, EDIT])
WITH mec AS (SELECT id FROM roles WHERE name='MECANICO'),
     inst AS (SELECT id FROM roles WHERE name='INSTRUCTOR')
INSERT INTO role_permissions(role_id, module_code, action)
SELECT mec.id, rp.module_code, rp.action
FROM mec, inst, role_permissions rp
WHERE rp.role_id = (SELECT id FROM inst)
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp2
    WHERE rp2.role_id = (SELECT id FROM mec) AND rp2.module_code = rp.module_code AND rp2.action = rp.action
  );
