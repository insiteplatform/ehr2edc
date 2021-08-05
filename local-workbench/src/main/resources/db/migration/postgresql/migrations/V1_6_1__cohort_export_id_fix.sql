DO $$
BEGIN
IF not EXISTS (SELECT * FROM information_schema.sequences
    WHERE sequence_name = 'cohort_export_id_seq') THEN
    CREATE SEQUENCE cohort_export_id_seq;
	ALTER TABLE cohort_export ALTER COLUMN id SET NOT NULL;
	ALTER TABLE cohort_export ALTER COLUMN id SET DEFAULT nextval('cohort_export_id_seq');
	ALTER SEQUENCE cohort_export_id_seq OWNED BY cohort_export.id;
END IF;
END $$;