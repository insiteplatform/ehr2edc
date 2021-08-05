ALTER TABLE feasibility_study
  ADD COLUMN lastModifiedDate timestamp without time zone,
  ADD COLUMN StudyStatus character varying(255) DEFAULT NULL,
  ADD COLUMN publishedDate timestamp without time zone,
  ALTER COLUMN status DROP NOT NULL;