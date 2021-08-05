ALTER TABLE feasibility_study ADD COLUMN creationType character varying(255) DEFAULT NULL;
UPDATE feasibility_study SET creationType = 'REGULAR' WHERE creationType IS NULL;
ALTER TABLE feasibility_study ALTER creationType SET NOT NULL;