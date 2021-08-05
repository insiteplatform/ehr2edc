ALTER TABLE cohort_export 
ADD COLUMN factsStartDate timestamp without time zone DEFAULT NULL,
ADD COLUMN factsEndDate timestamp without time zone DEFAULT NULL,
ADD COLUMN errorMessage character varying(255) DEFAULT NULL;