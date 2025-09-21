-- V4: Add users.is_active and user_attributes key-value store

-- 1) Add is_active column with default true, then set NOT NULL
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active boolean;
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;
ALTER TABLE users ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE users ALTER COLUMN is_active SET DEFAULT TRUE;

-- 2) Create user_attributes table for @ElementCollection Map<Enum, String>
CREATE TABLE IF NOT EXISTS user_attributes (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attr_key VARCHAR(50) NOT NULL,
    attr_value VARCHAR(255),
    CONSTRAINT user_attributes_pk PRIMARY KEY (user_id, attr_key)
);

-- 3) Optional index by key
CREATE INDEX IF NOT EXISTS idx_user_attributes_key ON user_attributes(attr_key);
