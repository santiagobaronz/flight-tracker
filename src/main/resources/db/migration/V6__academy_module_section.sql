DO $$
BEGIN
    IF to_regclass('public.academy_modules') IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'academy_modules'
              AND column_name = 'section'
        ) THEN
            EXECUTE 'ALTER TABLE public.academy_modules ADD COLUMN section VARCHAR(20) NOT NULL DEFAULT ''APPLICATION''';
        END IF;
        EXECUTE 'UPDATE public.academy_modules SET section = ''APPLICATION'' WHERE section IS NULL';
    ELSE
        RAISE NOTICE 'Table public.academy_modules does not exist; skipping V6 changes.';
    END IF;
END$$;
