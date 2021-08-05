CREATE TABLE observation_fact_copy
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
  text_search_index integer NOT NULL DEFAULT nextval('observation_fact_text_search_index_seq'::regclass),
  sourcesystem_key character varying(200),
  val_lln numeric(18,5),
  val_uln numeric(18,5),
  observation_id bigint NOT NULL

)
WITH (
  OIDS=FALSE
);

ALTER TABLE observation_fact_copy
  OWNER TO i2b2demodata;

ALTER SEQUENCE observation_fact_text_search_index_seq RESTART WITH 1;
ALTER SEQUENCE observation_fact_text_search_index_seq START WITH 1;
ALTER SEQUENCE observation_fact_text_search_index_seq OWNED BY observation_fact_copy.text_search_index;

INSERT INTO observation_fact_copy (encounter_num ,patient_num,concept_cd,provider_id,start_date,modifier_cd,
instance_num,valtype_cd,tval_char,nval_num,valueflag_cd,quantity_num,units_cd,end_date,location_cd,observation_blob,
confidence_num,update_date,download_date,import_date,sourcesystem_cd,upload_id, sourcesystem_key, val_lln,val_uln,observation_id)
SELECT o.encounter_num ,o.patient_num,o.concept_cd,o.provider_id,o.start_date,o.modifier_cd,o.instance_num,o.valtype_cd,o.tval_char,
o.nval_num,o.valueflag_cd,o.quantity_num,o.units_cd,o.end_date,o.location_cd,o.observation_blob,o.confidence_num,o.update_date,
o.download_date,o.import_date,o.sourcesystem_cd,o.upload_id, o.sourcesystem_key,
(case when o.modifier_cd = '@' then lln.nval_num else null end) as val_lln, 
(case when o.modifier_cd = '@' then uln.nval_num else null end) as val_uln,
dense_rank() OVER (ORDER BY o.concept_cd, o.patient_num, o.start_date, o.encounter_num, o.provider_id, o.instance_num) as observation_id
FROM observation_fact o 
LEFT OUTER JOIN observation_fact lln 
ON o.encounter_num = lln.encounter_num
AND o.patient_num = lln.patient_num
AND o.concept_cd = lln.concept_cd
AND o.provider_id = lln.provider_id
AND o.start_date = lln.start_date
AND o.instance_num = lln.instance_num
AND lln.modifier_cd = 'MOD:LLN'
LEFT OUTER JOIN observation_fact uln 
ON o.encounter_num = uln.encounter_num
AND o.patient_num = uln.patient_num
AND o.concept_cd = uln.concept_cd
AND o.provider_id = uln.provider_id
AND o.start_date = uln.start_date
AND o.instance_num = uln.instance_num
AND uln.modifier_cd = 'MOD:ULN'
ORDER BY o.concept_cd, o.patient_num, o.start_date, o.encounter_num, o.provider_id, o.instance_num, o.modifier_cd;

ALTER TABLE ONLY observation_fact_copy
    ADD CONSTRAINT observation_fact_pk_copy PRIMARY KEY (concept_cd, patient_num, start_date, encounter_num, provider_id, instance_num, modifier_cd);
ALTER TABLE observation_fact_copy CLUSTER ON observation_fact_pk_copy;

CREATE INDEX of_idx_encounter_copy
  ON observation_fact_copy
  USING btree
  (encounter_num);

CREATE INDEX of_idx_modifier_copy
  ON observation_fact_copy
  USING btree
  (modifier_cd COLLATE pg_catalog."default");

CREATE INDEX of_idx_patient_num_copy
  ON observation_fact_copy
  USING btree
  (patient_num);
  
CREATE INDEX of_idx_observation_id_copy
  ON observation_fact_copy
  USING btree
  (observation_id);

CREATE INDEX of_idx_sourcesystem_cd_key_copy
  ON observation_fact_copy
  USING btree
  (sourcesystem_cd COLLATE pg_catalog."default", sourcesystem_key COLLATE pg_catalog."default");
  
 
BEGIN;

ALTER TABLE observation_fact       RENAME TO   observation_fact_old;
ALTER TABLE   observation_fact_copy RENAME TO observation_fact  ;
DROP TABLE   observation_fact_old CASCADE;
                                                                             
ALTER INDEX        observation_fact_pk_copy RENAME TO        observation_fact_pk;
ALTER INDEX           of_idx_encounter_copy RENAME TO of_idx_encounter          ;
ALTER INDEX            of_idx_modifier_copy RENAME TO of_idx_modifier           ;
ALTER INDEX         of_idx_patient_num_copy RENAME TO of_idx_patient_num        ;
ALTER INDEX      of_idx_observation_id_copy RENAME TO of_idx_observation_id     ;
ALTER INDEX of_idx_sourcesystem_cd_key_copy RENAME TO of_idx_sourcesystem_cd_key;

ALTER SEQUENCE observation_fact_text_search_index_seq OWNED BY observation_fact.text_search_index;

COMMIT;

ALTER TABLE archive_observation_fact ADD COLUMN val_lln numeric(18,5);
ALTER TABLE archive_observation_fact ADD COLUMN val_uln numeric(18,5);
ALTER TABLE archive_observation_fact ADD COLUMN observation_id bigint;