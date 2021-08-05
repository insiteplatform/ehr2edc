CREATE TABLE analysis_measurement_distribution (
  id bigint NOT NULL,
  chosenUnitCode character varying(255),
  referenceDate timestamp without time zone,
  conceptCategory character varying(255),
  conceptId character varying(255),
  conceptCoding character varying(255),
  conceptOid character varying(255),
  conceptLabel character varying(255),
  conceptQualifier character varying(255),
  conceptUrn character varying(255),
  conceptUnit character varying(255),
  conceptOperator character varying(255),
  conceptValue character varying(255),
  conceptDefaultUnit character varying(255),
  conceptRuleType character varying(255),
  conceptUnitLabel character varying(255),
  conceptUnitId character varying(255),
  conceptUnitCoding character varying(255),
  conceptUnitOid character varying(255),
  consideredFactsCount integer,
  CONSTRAINT analysis_measurement_distr_pkey PRIMARY KEY (id),
  CONSTRAINT fk_measurement_distr_analysisId FOREIGN KEY (id) 
  	REFERENCES analysis (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis
  OWNER TO postgres;

ALTER TABLE analysis_result_binning_bins
ALTER COLUMN "count" TYPE double precision;

ALTER TABLE analysis_prevalence
ADD COLUMN clinicalFactsStartDate timestamp without time zone,
ADD COLUMN clinicalFactsEndDate timestamp without time zone;

CREATE TABLE IF NOT EXISTS hibernate_sequences
(
  sequence_name character varying(255),
  sequence_next_hi_value integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE hibernate_sequences
  OWNER TO postgres;
  
CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 31
  CACHE 1;
ALTER TABLE hibernate_sequence
  OWNER TO postgres;

