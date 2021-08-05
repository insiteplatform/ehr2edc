-- create copy table
CREATE TABLE observation_fact_new
(
  encounter_num integer NOT NULL,
  patient_num integer NOT NULL,
  concept_cd character varying(50) NOT NULL,
  provider_id character varying(50) NOT NULL,
  start_date timestamp without time zone NOT NULL,
  modifier_cd character varying(100) NOT NULL DEFAULT '@'::character varying,
  instance_num integer NOT NULL DEFAULT 1,
  valtype_cd character varying(50),
  tval_char character varying(255),
  nval_num numeric(18,5),
  valueflag_cd character varying(50),
  quantity_num numeric(18,5),
  units_cd character varying(50),
  end_date timestamp without time zone,
  location_cd character varying(50),
  observation_blob text,
  confidence_num numeric(18,5),
  update_date timestamp without time zone,
  download_date timestamp without time zone,
  import_date timestamp without time zone,
  sourcesystem_cd character varying(50),
  upload_id integer,
  text_search_index integer NOT NULL,
  sourcesystem_key character varying(200)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE observation_fact_new
  OWNER TO i2b2demodata;

-- insert rows with extra 
insert into OBSERVATION_FACT_new (  encounter_num,
  patient_num,
  concept_cd,
  provider_id,
  start_date,
  modifier_cd,
  instance_num,
  valtype_cd,
  tval_char,
  nval_num,
  valueflag_cd,
  quantity_num,
  units_cd,
  end_date,
  location_cd,
  observation_blob,
  confidence_num,
  update_date,
  download_date,
  import_date,
  sourcesystem_cd,
  upload_id,
  text_search_index,
  sourcesystem_key
)
select 
  encounter_num,
  patient_num,
  concept_cd,
  provider_id,
  start_date,
  modifier_cd,
  instance_num,
  valtype_cd,
  tval_char,
  nval_num,
  valueflag_cd,
  quantity_num,
  units_cd,
  end_date,
  location_cd,
  observation_blob,
  confidence_num,
  update_date,
  download_date,
  import_date,
  sourcesystem_cd,
  upload_id,
  text_search_index,
  NULL as sourcesystem_key
from OBSERVATION_FACT a
group by concept_cd, patient_num, start_date, encounter_num, provider_id, instance_num, modifier_cd;

alter table observation_fact_new add constraint observation_fact_new_pk PRIMARY KEY (concept_cd, patient_num, start_date, encounter_num, provider_id, instance_num, modifier_cd);

--Finally: create indices and replace old with new

ALTER TABLE observation_fact_new CLUSTER ON observation_fact_new_pk;
ANALYZE observation_fact_new;
CREATE INDEX observation_fact_new_idx_modifier ON observation_fact_new USING btree (modifier_cd COLLATE pg_catalog."default");
CREATE INDEX observation_fact_new_idx_encounter ON observation_fact_new USING btree (encounter_num);
CREATE INDEX observation_fact_new_idx_patient_num ON observation_fact_new USING btree (patient_num);
CREATE INDEX observation_fact_new_idx_sourcesystem_cd_key ON observation_fact_new USING btree (sourcesystem_cd, sourcesystem_key);
ALTER TABLE observation_fact RENAME TO observation_fact_to_be_deleted;
ALTER SEQUENCE observation_fact_text_search_index_seq OWNED BY observation_fact_new.text_search_index;
ALTER TABLE observation_fact_new RENAME TO observation_fact;
DROP TABLE observation_fact_to_be_deleted CASCADE;
ALTER TABLE observation_fact ALTER COLUMN text_search_index SET DEFAULT nextval('observation_fact_text_search_index_seq');

ALTER TABLE observation_fact RENAME CONSTRAINT observation_fact_new_pk TO observation_fact_pk;
ALTER INDEX observation_fact_new_idx_modifier RENAME TO of_idx_modifier;
ALTER INDEX observation_fact_new_idx_encounter RENAME TO of_idx_encounter;
ALTER INDEX observation_fact_new_idx_patient_num RENAME TO of_idx_patient_num;
ALTER INDEX observation_fact_new_idx_sourcesystem_cd_key RENAME TO of_idx_sourcesystem_cd_key;
ANALYZE observation_fact;

ALTER TABLE archive_observation_fact ADD COLUMN sourcesystem_key character varying(200);