ALTER TABLE clinical_study_cohort ADD COLUMN status character varying(255) DEFAULT NULL;

UPDATE clinical_study_cohort SET status = 'SUCCEEDED' WHERE patientcount IS NOT NULL;
UPDATE clinical_study_cohort SET status = 'FAILED' WHERE message IS NOT NULL AND message != '';
UPDATE clinical_study_cohort SET status = 'CALCULATING' WHERE patientcount IS NULL AND message IS NULL;
