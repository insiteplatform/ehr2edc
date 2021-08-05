CREATE TABLE clinical_patient_exclusion (
  id bigint NOT NULL,
  dateExcluded timestamp without time zone DEFAULT NULL,
  exclusionReason character varying DEFAULT NULL,
  originalCohortType character varying DEFAULT NULL,
  patientId character varying DEFAULT NULL,
  excludedBy_id bigint DEFAULT NULL,
  screeningFilter_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_patient_exclusion PRIMARY KEY (id),
  CONSTRAINT FK_patient_exclusion_user FOREIGN KEY (excludedBy_id) REFERENCES app_user (id),
  CONSTRAINT FK_patient_exclusion_filter FOREIGN KEY (screeningFilter_id) REFERENCES clinical_study_protocol_version_filter (id)
);