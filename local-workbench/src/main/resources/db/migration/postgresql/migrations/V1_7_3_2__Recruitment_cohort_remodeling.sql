ALTER TABLE clinical_study_cohort
	ADD column study_id bigint,
    ADD CONSTRAINT FK_clinical_study_cohort FOREIGN KEY (study_id) REFERENCES clinical_study(id);

UPDATE clinical_study_cohort studyCohort
SET study_id = (SELECT version.study_id
	FROM clinical_study_protocol_screening_result result
    JOIN clinical_study_protocol_version_filter filter ON (result.protocolScreeningFilter_id = filter.id)
    JOIN clinical_study_protocol_version version ON (filter.protocolVersion_id = version.id)
    WHERE result.id = studyCohort.screeningResult_id)
WHERE study_id IS NULL;
    
UPDATE clinical_study_cohort studyCohort
SET screeningResult_id=NULL
WHERE cohortType IN ('VERIFIED_INCLUDED', 'VERIFIED_EXCLUDED', 'ENROLLED_INCLUDED', 'ENROLLED_EXCLUDED', 'AWAITING_CONSENT', 'TO_BE_SCREENED');