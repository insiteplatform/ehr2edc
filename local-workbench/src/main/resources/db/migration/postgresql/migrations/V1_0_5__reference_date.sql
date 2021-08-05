ALTER TABLE cohort 
ADD COLUMN referenceDate timestamp without time zone DEFAULT NULL;

update cohort
	set referenceDate = creationDate
    where referenceDate is null and id != 0;