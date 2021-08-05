CREATE TABLE feasibility_study (
    id bigint NOT NULL,
    externalId character varying(255) NOT NULL,
    name character varying(400) NOT NULL,
    description text,
    sponsor character varying(255) NOT NULL,
    status integer NOT NULL,
    placementRequestDate timestamp without time zone,

    CONSTRAINT PK_feasibility_study PRIMARY KEY (id)
);