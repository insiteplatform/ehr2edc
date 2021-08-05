CREATE TABLE cohort_export
(
  id bigint NOT NULL,
  cohortId bigint,
  user_id bigint,
  fileName character varying(80),
  filePath character varying(255),
  size character varying(80),
  date timestamp without time zone,
  type integer,
  status integer,
  CONSTRAINT cohort_export_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohortExport_AppUser_id FOREIGN KEY (user_id)
	REFERENCES AppUser (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohortExport_cohort_id FOREIGN KEY (cohortId)
	REFERENCES cohort (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_export
  OWNER TO postgres;
