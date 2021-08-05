INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'EXPORT_COHORT_RECRUITMENT_STUDY');

CREATE TABLE clinical_study_cohort_export (
	id bigint NOT NULL,
	date timestamp without time zone,
	completiondate timestamp without time zone,
    filename character varying(255),
    filepath character varying(255),
    size bigint,
    status character varying(255),
    resultformattype character varying(255),
    cohortid bigint,
    user_id bigint,
    errormessage character varying(255) DEFAULT NULL::character varying,
    CONSTRAINT clinical_study_cohort_export_pkey PRIMARY KEY (id),
    CONSTRAINT fk_clinicalStudyCohortExport_appUser_id FOREIGN KEY (user_id)
        REFERENCES app_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_clinicalStudyCohortExport_cohort_id FOREIGN KEY (cohortId)
        REFERENCES clinical_study_cohort (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);