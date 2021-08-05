CREATE TABLE overridden_auto_approval_settings (
	id bigint NOT NULL,
	status character varying(255),
	intervalValue integer,
	intervalUnit character varying(255),
	CONSTRAINT PK_overridden_auto_approval_settings PRIMARY KEY (id)
);