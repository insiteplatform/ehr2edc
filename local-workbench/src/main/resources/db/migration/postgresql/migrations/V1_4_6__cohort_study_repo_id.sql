ALTER TABLE cohort_study
  ADD COLUMN repoStudyId character varying(255) DEFAULT NULL;
  
UPDATE cohort_study
  SET repoStudyId = uuid_in(md5(random()::text || now()::text)::cstring);