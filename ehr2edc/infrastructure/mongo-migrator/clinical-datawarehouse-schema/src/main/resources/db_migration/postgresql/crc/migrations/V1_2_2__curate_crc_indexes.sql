DROP INDEX OF_IDX_START_DATE;
DROP INDEX OF_IDX_MODIFIER_CONCEPT;
DROP INDEX PD_IDX_SEXCD;
DROP INDEX VD_IDX_INOUTCD;
DROP INDEX VD_IDX_DATES;
DROP INDEX CL_IDX_UPLOADID;
DROP INDEX pk_archive_obsfact;

CREATE INDEX pd_idx_provider_id ON provider_dimension USING btree (provider_id);
CREATE INDEX pm_patnum_idx ON patient_mapping USING btree (patient_num);
CREATE UNIQUE INDEX pk_archive_obsfact ON archive_observation_fact USING btree (concept_cd, patient_num, start_date, encounter_num, provider_id, instance_num, modifier_cd,ARCHIVE_UPLOAD_ID);

CREATE UNIQUE INDEX encounter_mapping_pk_copy ON encounter_mapping USING btree (patient_ide, patient_ide_source, encounter_ide, encounter_ide_source,  project_id);
BEGIN;
ALTER TABLE encounter_mapping DROP CONSTRAINT encounter_mapping_pk;
ALTER TABLE encounter_mapping ADD CONSTRAINT encounter_mapping_pk PRIMARY KEY USING INDEX encounter_mapping_pk_copy;
COMMIT;
CREATE UNIQUE INDEX visit_dimension_pk_copy ON visit_dimension USING btree (patient_num, encounter_num);
BEGIN;
ALTER TABLE visit_dimension DROP CONSTRAINT visit_dimension_pk;
ALTER TABLE visit_dimension ADD CONSTRAINT visit_dimension_pk PRIMARY KEY USING INDEX visit_dimension_pk_copy;
COMMIT;