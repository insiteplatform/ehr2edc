CREATE TABLE analysis
(
  id bigint NOT NULL,
  creationtime timestamp without time zone,
  errormessage character varying(255),
  status integer,
  timewindowenddate timestamp without time zone,
  timewindowstartdate timestamp without time zone,
  description character varying(255),
  implementingclass character varying(255),
  name character varying(255),
  creator_id bigint,
  targetid bigint,
  CONSTRAINT analysis_pkey PRIMARY KEY (id),
  CONSTRAINT fk_analysis_AppUser_id FOREIGN KEY (creator_id)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_analysis_cohort_id FOREIGN KEY (targetid)
      REFERENCES cohort (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis
  OWNER TO postgres;

CREATE TABLE analysis_age_distribution
(
  binsize smallint NOT NULL,
  totalbins smallint NOT NULL,
  id bigint NOT NULL,
  CONSTRAINT analysis_age_distribution_pkey PRIMARY KEY (id),
  CONSTRAINT fk_ageDistribution_analysisId FOREIGN KEY (id)
      REFERENCES analysis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_age_distribution
  OWNER TO postgres;

CREATE TABLE analysis_cohort_comparison
(
  id bigint NOT NULL,
  comparedid bigint,
  CONSTRAINT analysis_cohort_comparison_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohortComparison_analysisId FOREIGN KEY (id)
      REFERENCES analysis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohortComparison_cohortId FOREIGN KEY (comparedid)
      REFERENCES cohort (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_cohort_comparison
  OWNER TO postgres;

CREATE TABLE analysis_gender_distribution
(
  id bigint NOT NULL,
  CONSTRAINT analysis_gender_distribution_pkey PRIMARY KEY (id),
  CONSTRAINT fk_genderDistribution_analysisId FOREIGN KEY (id)
      REFERENCES analysis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_gender_distribution
  OWNER TO postgres;

CREATE TABLE analysis_prevalence
(
  code character varying(255),
  codingsystemname character varying(255),
  codingsystemoid character varying(255),
  text character varying(255),
  hierarchylevelofreporting integer NOT NULL,
  id bigint NOT NULL,
  CONSTRAINT analysis_prevalence_pkey PRIMARY KEY (id),
  CONSTRAINT fk_prevalence_analysisId FOREIGN KEY (id)
      REFERENCES analysis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_prevalence
  OWNER TO postgres;

CREATE TABLE analysis_result_binning
(
  id bigint NOT NULL,
  executionendtime timestamp without time zone,
  executionstarttime timestamp without time zone,
  analysisid bigint,
  CONSTRAINT analysis_result_binning_pkey PRIMARY KEY (id),
  CONSTRAINT fk_resultBinning_analysisId FOREIGN KEY (analysisid)
      REFERENCES analysis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_result_binning
  OWNER TO postgres;

CREATE TABLE analysis_result_binning_bins
(
  analysis_binning_result_id bigint NOT NULL,
  count bigint NOT NULL,
  variablelabel character varying(255),
  variablename character varying(255),
  CONSTRAINT fk_resultBinningBins_analysisId FOREIGN KEY (analysis_binning_result_id)
      REFERENCES analysis_result_binning (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE analysis_result_binning_bins
  OWNER TO postgres;
  


