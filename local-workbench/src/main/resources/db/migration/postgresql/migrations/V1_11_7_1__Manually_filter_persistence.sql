CREATE TABLE clinical_study_manually_filtered_cohort (
    id bigint NOT NULL,
    cohort_id bigint NOT NULL,
    queryContent text NOT NULL,

    CONSTRAINT PK_clinical_study_manually_filtered_cohort PRIMARY KEY (id),
    CONSTRAINT FK_clinical_study_manually_filtered_cohort FOREIGN KEY (cohort_id) REFERENCES clinical_study_cohort (id)
);