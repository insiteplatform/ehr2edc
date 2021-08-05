CREATE TABLE authentication_attempt (
	id bigint NOT NULL,
	email character varying(255) NOT NULL,
	result character varying(255) NOT NULL,
    attempt_timestamp timestamp without time zone NOT NULL,
	CONSTRAINT PK_authentication_attempt PRIMARY KEY (id)
);

CREATE INDEX idx_email ON authentication_attempt (email);