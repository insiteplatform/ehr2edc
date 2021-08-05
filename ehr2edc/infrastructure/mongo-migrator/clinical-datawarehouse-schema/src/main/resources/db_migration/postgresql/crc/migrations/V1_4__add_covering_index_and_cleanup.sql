BEGIN;
DROP INDEX of_idx_sourcesystem_cd_key;
COMMIT;

CREATE INDEX of_idx_modcd_concd_patnum_start_encnum_obsid_end_valnum_uln_lln
  ON observation_fact
  USING btree
  (modifier_cd COLLATE pg_catalog."default", concept_cd COLLATE pg_catalog."default", patient_num, start_date, encounter_num, observation_id, end_date, nval_num, val_uln, val_lln, units_cd COLLATE pg_catalog."default");

ALTER TABLE observation_fact DROP CONSTRAINT observation_fact_pk;

DROP INDEX of_idx_modifier;