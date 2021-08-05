ALTER TABLE observation_fact DROP CONSTRAINT observation_fact_pk;
CREATE UNIQUE INDEX observation_fact_pk_idx ON observation_fact USING btree (concept_cd, patient_num, start_date, encounter_num, provider_id, instance_num, modifier_cd);
ALTER TABLE observation_fact ADD CONSTRAINT observation_fact_pk PRIMARY KEY USING INDEX observation_fact_pk_idx;