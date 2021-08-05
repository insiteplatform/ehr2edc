-- Table: AppUser

-- DROP TABLE AppUser;

CREATE TABLE AppUser
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  email character varying(80),
  enabled boolean NOT NULL,
  first_name character varying(80),
  deleted boolean,
  last_name character varying(80),
  password character varying(80) NOT NULL,
  username character varying(80) NOT NULL,
  CONSTRAINT AppUser_pkey PRIMARY KEY (id),
  CONSTRAINT uk_username UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE AppUser
  OWNER TO postgres;

-- Table: authority

-- DROP TABLE authority;

CREATE TABLE authority
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  name character varying(80) NOT NULL,
  CONSTRAINT authority_pkey PRIMARY KEY (id),
  CONSTRAINT uk_auhority_name UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE authority
  OWNER TO postgres;

-- Table: cohort_study

-- DROP TABLE cohort_study;

CREATE TABLE cohort_study
(
  id bigserial NOT NULL,
  creationdate timestamp without time zone,
  description text,
  name character varying(255),
  author_id bigint,
  CONSTRAINT cohort_study_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohortStudy_AppUser_id FOREIGN KEY (author_id)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_study
  OWNER TO postgres;

-- Table: cohort

-- DROP TABLE cohort;

CREATE TABLE cohort
(
  id bigserial NOT NULL,
  creationdate timestamp without time zone,
  description text,
  name character varying(255),
  patientcount bigint,
  repokey character varying(255),
  author_id bigint,
  cstudyid bigint,
  source_id bigint,
  CONSTRAINT cohort_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohort_cohort_id FOREIGN KEY (source_id)
      REFERENCES cohort (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohort_AppUser_id FOREIGN KEY (author_id)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohort_cohortStudy_id FOREIGN KEY (cstudyid)
      REFERENCES cohort_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort
  OWNER TO postgres;

-- Table: physicians

-- DROP TABLE physicians;

CREATE TABLE physicians
(
  id bigserial NOT NULL,
  isdefault boolean,
  providerid character varying(255),
  userid bigint,
  CONSTRAINT physicians_pkey PRIMARY KEY (id),
  CONSTRAINT fk_physicians_AppUser_id FOREIGN KEY (userid)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE physicians
  OWNER TO postgres;


-- Table: patient

-- DROP TABLE patient;

CREATE TABLE patient
(
  patientid bigint NOT NULL,
  age smallint,
  gender character varying(255),
  tpid bigint,
  CONSTRAINT patient_pkey PRIMARY KEY (patientid),
  CONSTRAINT fk_patient_physicians_id FOREIGN KEY (tpid)
      REFERENCES physicians (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE patient
  OWNER TO postgres;

-- Table: cohort_patient

-- DROP TABLE cohort_patient;

CREATE TABLE cohort_patient
(
  id bigserial NOT NULL,
  dateadded timestamp without time zone,
  cohortid bigint,
  patientid bigint,
  userid bigint,
  CONSTRAINT cohort_patient_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cohortPatient_cohort_id FOREIGN KEY (cohortid)
      REFERENCES cohort (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohortPatient_patient_patientId FOREIGN KEY (patientid)
      REFERENCES patient (patientid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cohortPatient_AppUser_id FOREIGN KEY (userid)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_cohortId_patientId UNIQUE (cohortid, patientid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_patient
  OWNER TO postgres;

-- Table: coverageTask

-- DROP TABLE coverageTask;

CREATE TABLE coverageTask
(
  id bigserial NOT NULL,
  completed boolean,
  completedconcepts bigint,
  maxlevel bigint,
  startdate timestamp without time zone,
  totalconcepts bigint,
  CONSTRAINT coverageTask_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE coverageTask
  OWNER TO postgres;

-- Table: coverageItem

-- DROP TABLE coverageItem;

CREATE TABLE coverageItem
(
  coverageid bigserial NOT NULL,
  count bigint,
  date timestamp without time zone,
  error character varying(255),
  code character varying(255),
  base character varying(255),
  state integer,
  task_id bigint,
  CONSTRAINT coverageItem_pkey PRIMARY KEY (coverageid),
  CONSTRAINT fk_coverageItem_coverageTask_id FOREIGN KEY (task_id)
      REFERENCES coverageTask (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE coverageItem
  OWNER TO postgres;

-- Index: index_oid_id

-- DROP INDEX index_oid_id;

CREATE INDEX index_oid_id
  ON coverageItem
  USING btree
  (base COLLATE pg_catalog."default", code COLLATE pg_catalog."default");


-- Table: user_authority

-- DROP TABLE user_authority;

CREATE TABLE user_authority
(
  user_id bigint NOT NULL,
  authority_id bigint NOT NULL,
  CONSTRAINT user_authority_pkey PRIMARY KEY (user_id, authority_id),
  CONSTRAINT fk_userAuthority_AppUser_id FOREIGN KEY (user_id)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_userAuthority_authority_id FOREIGN KEY (authority_id)
      REFERENCES authority (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_authority
  OWNER TO postgres;

-- Table: usergroup

-- DROP TABLE usergroup;

CREATE TABLE usergroup
(
  id bigint NOT NULL,
  version bigint NOT NULL,
  name character varying(80) NOT NULL,
  CONSTRAINT usergroup_pkey PRIMARY KEY (id),
  CONSTRAINT uk_usergroup_name UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usergroup
  OWNER TO postgres;

-- Table: usergroup_authority

-- DROP TABLE usergroup_authority;

CREATE TABLE usergroup_authority
(
  group_id bigint NOT NULL,
  authority_id bigint NOT NULL,
  CONSTRAINT usergroup_authority_pkey PRIMARY KEY (group_id, authority_id),
  CONSTRAINT fk_usergroupAuthority_authority_id FOREIGN KEY (authority_id)
      REFERENCES authority (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_usergroupAuthority_usergroup_id FOREIGN KEY (group_id)
      REFERENCES usergroup (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usergroup_authority
  OWNER TO postgres;

-- Table: usergroup_user

-- DROP TABLE usergroup_user;

CREATE TABLE usergroup_user
(
  group_id bigint NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT usergroup_user_pkey PRIMARY KEY (group_id, user_id),
  CONSTRAINT usergroupUser_AppUser_id FOREIGN KEY (user_id)
      REFERENCES AppUser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_usergroupUser_usergroup_id FOREIGN KEY (group_id)
      REFERENCES usergroup (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usergroup_user
  OWNER TO postgres;