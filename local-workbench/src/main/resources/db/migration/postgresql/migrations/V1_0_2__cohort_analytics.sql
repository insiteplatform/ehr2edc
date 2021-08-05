CREATE TABLE cohort_analytics_data (
  id bigint NOT NULL,
  type bigint,
  cohortId bigint,
  CONSTRAINT cohort_analytics_data_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohort_analytics_data_cohortId FOREIGN KEY (cohortId)
	  REFERENCES cohort (id) MATCH SIMPLE
	  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_analytics_data
  OWNER TO postgres;

CREATE TABLE cohort_analytics_data_bins (
  cohort_analytics_data_id bigint NOT NULL,
  binCount bigint,
  bin character varying(255),
  CONSTRAINT analytics_data_bins_pkey PRIMARY KEY (cohort_analytics_data_id, bin),
  CONSTRAINT fk_analytics_data_id FOREIGN KEY (cohort_analytics_data_id)
	  REFERENCES cohort_analytics_data (id) MATCH SIMPLE
	  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_analytics_data_bins
  OWNER TO postgres;
