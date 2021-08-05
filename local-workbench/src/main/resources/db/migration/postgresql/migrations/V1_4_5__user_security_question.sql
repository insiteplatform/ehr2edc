ALTER TABLE app_user
  ADD COLUMN securityAnswer character varying(255) DEFAULT NULL,
  ADD COLUMN securityQuestion character varying(255) DEFAULT NULL;