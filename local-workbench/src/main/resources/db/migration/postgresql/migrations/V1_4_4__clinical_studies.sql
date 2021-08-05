CREATE TABLE clinical_study (
  id bigint NOT NULL,
  archived boolean DEFAULT NULL,
  deadline timestamp without time zone DEFAULT NULL,
  description text,
  goal bigint DEFAULT NULL,
  name character varying(255) DEFAULT NULL,
  state character varying(255) DEFAULT NULL,
  sponsor_id bigint DEFAULT NULL,
  currentVersion_id bigint DEFAULT NULL,
  lastUpdated timestamp without time zone DEFAULT NULL,
  reached bigint DEFAULT NULL,
  creationDate timestamp without time zone DEFAULT NULL,
  CONSTRAINT pk_clinical_study PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_acceptance (
  id bigint NOT NULL,
  acceptedDate timestamp without time zone DEFAULT NULL,
  coordinator_id bigint DEFAULT NULL,
  study_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_study_acceptance PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_investigators (
  clinical_study_id bigint NOT NULL,
  investigator_id bigint NOT NULL,
  CONSTRAINT pk_clinical_study_investigators PRIMARY KEY(clinical_study_id, investigator_id)
  
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_protocol_version (
  id bigint NOT NULL,
  version character varying(255) DEFAULT NULL,
  study_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_study_protocol_version PRIMARY KEY (id)  
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_protocol_version_document (
  id bigint NOT NULL,
  file oid,
  fileName character varying(255) DEFAULT NULL,
  fileSize bigint DEFAULT NULL,
  fileType character varying(255) DEFAULT NULL,
  protocolVersion_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_study_protocol_version_document PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_protocol_version_filter (
  id bigint NOT NULL,
  archived boolean DEFAULT NULL,
  fromSponsor boolean DEFAULT NULL,
  jsonContent text,
  version character varying(255) DEFAULT NULL,
  protocolVersion_id bigint DEFAULT NULL,
  creationDate timestamp without time zone DEFAULT NULL,
  creator_id bigint DEFAULT NULL,
  sponsor_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_study_protocol_version_filter PRIMARY KEY (id)
  
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_protocol_version_result (
  id bigint NOT NULL,
  candidatesCount bigint DEFAULT NULL,
  candidatesKey character varying(255) DEFAULT NULL,
  repoKey character varying(255) DEFAULT NULL,
  suggestedExcludedCount bigint DEFAULT NULL,
  suggestedExcludedKey character varying(255) DEFAULT NULL,
  suggestedIncludedCount bigint DEFAULT NULL,
  suggestedIncludedKey character varying(255) DEFAULT NULL,
  verifiedExcludedCount bigint DEFAULT NULL,
  verifiedExcludedKey character varying(255) DEFAULT NULL,
  verifiedIncludedCount bigint DEFAULT NULL,
  verifiedIncludedKey character varying(255) DEFAULT NULL,
  protocolVersion_id bigint DEFAULT NULL,
  CONSTRAINT pk_clinical_study_protocol_version_result PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE external_user (
  id bigint NOT NULL,
  companyName character varying(255) DEFAULT NULL,
  email character varying(255) DEFAULT NULL,
  name character varying(255) DEFAULT NULL,
  CONSTRAINT pk_external_user PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE principal_investigator (
  id bigint NOT NULL,
  occupied boolean DEFAULT NULL,
  userId bigint DEFAULT NULL,
  CONSTRAINT pk_principal_investigator PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clinical_study_patient_assignment (
  id bigint NOT NULL,
  assignmentType character varying(255) DEFAULT NULL,
  patientId bigint DEFAULT NULL,
  protocolVersion_id bigint DEFAULT NULL,
  user_id bigint DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_assignment_protocol_version FOREIGN KEY (protocolVersion_id) REFERENCES clinical_study_protocol_version (id),
  CONSTRAINT FK_assignment_user FOREIGN KEY (user_id) REFERENCES app_user (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE clinical_study
	ADD CONSTRAINT FK_clinical_study_current_version FOREIGN KEY (currentVersion_id) REFERENCES clinical_study_protocol_version (id),
	ADD CONSTRAINT FK_clinical_study_sponsor FOREIGN KEY (sponsor_id) REFERENCES external_user (id);
	
ALTER TABLE clinical_study_acceptance
	ADD CONSTRAINT FK_clinical_study_acceptance_user FOREIGN KEY (coordinator_id) REFERENCES app_user (id),
	ADD CONSTRAINT FK_clinical_study_acceptance_study FOREIGN KEY (study_id) REFERENCES clinical_study (id);
	
ALTER TABLE clinical_study_investigators
	ADD CONSTRAINT FK_clinical_study_investigators_study FOREIGN KEY (clinical_study_id) REFERENCES clinical_study (id),
	ADD CONSTRAINT FK_clinical_study_investigators_investigator FOREIGN KEY (investigator_id) REFERENCES principal_investigator (id);
	
ALTER TABLE clinical_study_protocol_version
	ADD CONSTRAINT FK_clinical_study_protocol_version FOREIGN KEY (study_id) REFERENCES clinical_study (id);

ALTER TABLE clinical_study_protocol_version_document
	ADD CONSTRAINT FK_clinical_study_protocol_version_document FOREIGN KEY (protocolVersion_id) REFERENCES clinical_study_protocol_version (id);
	
ALTER TABLE clinical_study_protocol_version_filter
	ADD CONSTRAINT FK_clinical_study_protocol_version_filter_version FOREIGN KEY (protocolVersion_id) REFERENCES clinical_study_protocol_version (id),
	ADD CONSTRAINT FK_clinical_study_protocol_version_filter_sponsor FOREIGN KEY (sponsor_id) REFERENCES external_user (id),
	ADD CONSTRAINT FK_clinical_study_protocol_version_filter_creator FOREIGN KEY (creator_id) REFERENCES app_user (id);
	
ALTER TABLE clinical_study_protocol_version_result
	ADD CONSTRAINT FK_clinical_study_protocol_version_result FOREIGN KEY (protocolVersion_id) REFERENCES clinical_study_protocol_version (id);
	
ALTER TABLE principal_investigator
	ADD CONSTRAINT FK_principal_investigator_user FOREIGN KEY (userId) REFERENCES app_user (id);