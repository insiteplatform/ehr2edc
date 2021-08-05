CREATE TABLE cohort_filterblock (
  id bigserial NOT NULL,
  creationDate timestamp without time zone DEFAULT NULL,
  author_id bigint DEFAULT NULL,
  name character varying(255) DEFAULT NULL,
  description text,
  cohort_id bigint DEFAULT NULL,
  jsonQuery text,
  favorite boolean,
  CONSTRAINT pk_cohort_filterblock PRIMARY KEY (id),
  CONSTRAINT fk_cohort_filterblock FOREIGN KEY (cohort_id) REFERENCES cohort (id),
  CONSTRAINT fk_filterblock_user FOREIGN KEY (author_id) REFERENCES AppUser (id)
);