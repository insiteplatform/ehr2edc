CREATE TABLE clinical_study_patient_scoring_reference (
	id bigint NOT NULL,
	patientsFilter_id bigint NOT NULL,
	scoringFilter_id bigint NOT NULL,
	creationDate timestamp without time zone NULL,
	repoKey character varying(50) NOT NULL,
	
	CONSTRAINT pk_scoring_ref PRIMARY KEY (id),
	CONSTRAINT fk_scoring_ref_patients FOREIGN KEY (patientsFilter_id) REFERENCES clinical_study_protocol_version_filter(id),
	CONSTRAINT fk_scoring_ref_scoring FOREIGN KEY (scoringFilter_id) REFERENCES clinical_study_protocol_version_filter(id)
)