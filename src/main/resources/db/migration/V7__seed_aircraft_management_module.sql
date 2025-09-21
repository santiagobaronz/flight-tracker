-- Seed AIRCRAFT module in MANAGEMENT section for all academies
-- Ensure module_code allows 'AIRCRAFT'
ALTER TABLE academy_modules DROP CONSTRAINT IF EXISTS academy_modules_module_code_check;
ALTER TABLE academy_modules ADD CONSTRAINT academy_modules_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT'));

ALTER TABLE role_permissions DROP CONSTRAINT IF EXISTS role_permissions_module_code_check;
ALTER TABLE role_permissions ADD CONSTRAINT role_permissions_module_code_check CHECK (module_code IN ('HOURS','AIRCRAFT'));

INSERT INTO academy_modules (academy_id, section, module_code, active, name, description, route)
SELECT a.id, 'MANAGEMENT', 'AIRCRAFT', TRUE, 'Aeronaves', 'Gestión de aeronaves', '/mgmt/aircraft'
FROM academies a
WHERE NOT EXISTS (
    SELECT 1 FROM academy_modules m
    WHERE m.academy_id = a.id AND m.module_code = 'AIRCRAFT'
);

-- Grant VIEW to ADMIN, INSTRUCTOR, PILOT for AIRCRAFT
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, 'AIRCRAFT', 'VIEW'
FROM roles r
WHERE r.name IN ('ADMIN','INSTRUCTOR','PILOT')
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.module_code = 'AIRCRAFT' AND rp.action = 'VIEW'
  );

-- Grant CREATE, EDIT, DELETE to ADMIN for AIRCRAFT
INSERT INTO role_permissions(role_id, module_code, action)
SELECT r.id, 'AIRCRAFT', act
FROM roles r
CROSS JOIN (
    SELECT 'CREATE' AS act UNION ALL
    SELECT 'EDIT' UNION ALL
    SELECT 'DELETE'
) x
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.module_code = 'AIRCRAFT' AND rp.action = x.act
  );
