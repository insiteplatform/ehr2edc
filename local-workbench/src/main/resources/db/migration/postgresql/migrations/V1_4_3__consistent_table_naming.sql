ALTER TABLE analysis
DROP CONSTRAINT fk_analysis_appuser_id;
ALTER TABLE cohort
DROP CONSTRAINT fk_cohort_appuser_id;
ALTER TABLE cohort_export
DROP CONSTRAINT fk_cohortexport_appuser_id;
ALTER TABLE cohort_filterblock
DROP CONSTRAINT fk_filterblock_user;
ALTER TABLE cohort_patient
DROP CONSTRAINT fk_cohortpatient_appuser_id;
ALTER TABLE cohort_study
DROP CONSTRAINT fk_cohortstudy_appuser_id;
ALTER TABLE physicians
DROP CONSTRAINT fk_physicians_appuser_id;
ALTER TABLE user_authority
DROP CONSTRAINT fk_userauthority_appuser_id;
ALTER TABLE usergroup_user
DROP CONSTRAINT usergroupuser_appuser_id;

ALTER TABLE AppUser RENAME TO app_user;

ALTER TABLE analysis
ADD CONSTRAINT fk_analysis_appuser_id FOREIGN KEY (creator_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE cohort
ADD CONSTRAINT fk_cohort_appuser_id FOREIGN KEY (author_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE cohort_export
ADD CONSTRAINT fk_cohortexport_appuser_id FOREIGN KEY (user_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE cohort_filterblock
ADD CONSTRAINT fk_filterblock_user FOREIGN KEY (author_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE cohort_patient
ADD CONSTRAINT fk_cohortpatient_appuser_id FOREIGN KEY (userId)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE cohort_study
ADD CONSTRAINT fk_cohortstudy_appuser_id FOREIGN KEY (author_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE physicians
ADD CONSTRAINT fk_physicians_appuser_id FOREIGN KEY (userId)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE user_authority
ADD CONSTRAINT fk_userauthority_appuser_id FOREIGN KEY (user_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE usergroup_user
ADD CONSTRAINT fk_usergroupuser_appuser_id FOREIGN KEY (user_id)
  REFERENCES app_user (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE coverageItem
DROP CONSTRAINT fk_coverageitem_coveragetask_id;	

ALTER TABLE coverageTask RENAME TO coverage_task;

ALTER TABLE coverageItem
ADD CONSTRAINT fk_coverageitem_coveragetask_id FOREIGN KEY (task_id)
  REFERENCES coverage_task (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE coverageItem RENAME TO coverage_item;
