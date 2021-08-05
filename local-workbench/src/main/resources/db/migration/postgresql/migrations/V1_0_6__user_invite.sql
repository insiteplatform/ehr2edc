ALTER TABLE AppUser 
ADD COLUMN tempPassword character varying(80) DEFAULT NULL,
ADD COLUMN expirationDate timestamp without time zone DEFAULT NULL;