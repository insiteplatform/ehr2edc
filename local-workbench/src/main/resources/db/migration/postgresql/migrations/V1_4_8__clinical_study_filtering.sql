DROP TABLE clinical_study_protocol_version_result;

CREATE TABLE clinical_study_protocol_screening_result (
  id bigint NOT NULL,
  repoKey character varying DEFAULT NULL,
  protocolScreeningFilter_id bigint DEFAULT NULL,
  dateExecuted timestamp without time zone DEFAULT NULL,
  CONSTRAINT pk_clinical_study_protocol_screening_result PRIMARY KEY (id),
  CONSTRAINT FK_screening_filter_result FOREIGN KEY (protocolScreeningFilter_id) REFERENCES clinical_study_protocol_version_filter (id)
);

CREATE TABLE clinical_study_cohort (
  id bigint NOT NULL,
  cohortType character varying DEFAULT NULL,
  patientCount bigint DEFAULT NULL,
  repoKey character varying DEFAULT NULL,
  screeningResult_id bigint DEFAULT NULL,
  isCompleted boolean DEFAULT NULL,
  message character varying DEFAULT NULL,
  CONSTRAINT pk_clinical_study_cohort PRIMARY KEY (id),
  CONSTRAINT UK_clinical_cohort_result UNIQUE(screeningResult_id,cohortType),
  CONSTRAINT FK_clinical_cohort_result FOREIGN KEY (screeningResult_id) REFERENCES clinical_study_protocol_screening_result (id)
);

ALTER TABLE clinical_study
  ADD COLUMN currentScreeningFilters_id bigint DEFAULT NULL,
  ADD CONSTRAINT FK_clinical_study_current_filter FOREIGN KEY (currentScreeningFilters_id) REFERENCES clinical_study_protocol_version_filter (id);