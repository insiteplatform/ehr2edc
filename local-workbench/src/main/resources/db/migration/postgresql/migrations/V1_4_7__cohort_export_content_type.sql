ALTER TABLE cohort_export
  ADD COLUMN resultContentType integer;
  
ALTER TABLE cohort_export
  RENAME COLUMN "type" TO resultFormatType;
