CREATE TABLE feature (
	featureId character varying(255) NOT NULL,
	isEnabled boolean NOT NULL,
	CONSTRAINT pk_feature PRIMARY KEY (featureId)
);