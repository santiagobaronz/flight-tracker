DO $$
BEGIN
    -- Operate only if the expected table exists in current schema
    IF to_regclass('public.aircrafts') IS NOT NULL THEN
        -- Rename column tail_number -> registration if needed
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'aircrafts'
              AND column_name = 'registration'
        ) AND EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'aircrafts'
              AND column_name = 'tail_number'
        ) THEN
            EXECUTE 'ALTER TABLE public.aircrafts RENAME COLUMN tail_number TO registration';
        END IF;

        -- Normalize type values so they are compatible with enum values
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'aircrafts'
              AND column_name = 'type'
        ) THEN
            EXECUTE 'UPDATE public.aircrafts SET type = ''AIRCRAFT'' WHERE type IS NULL OR type NOT IN (''AIRCRAFT'',''SIMULATOR'')';
        END IF;

        -- Add is_active column if missing
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'aircrafts'
              AND column_name = 'is_active'
        ) THEN
            EXECUTE 'ALTER TABLE public.aircrafts ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE';
        END IF;

        -- Ensure unique index on (academy_id, registration) if registration exists
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'aircrafts'
              AND column_name = 'registration'
        ) THEN
            IF NOT EXISTS (
                SELECT 1 FROM pg_indexes
                WHERE schemaname = current_schema()
                  AND indexname = 'ux_aircrafts_academy_registration'
            ) THEN
                EXECUTE 'CREATE UNIQUE INDEX ux_aircrafts_academy_registration ON public.aircrafts (academy_id, registration)';
            END IF;
        END IF;
    ELSE
        RAISE NOTICE 'Table public.aircrafts does not exist; skipping V5 changes.';
    END IF;
END$$;
