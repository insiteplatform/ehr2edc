use selenium_lwb;

-- Selenium testuser
INSERT INTO `appuser` (`id`,`version`, `email`, `enabled`, `first_name`, `deleted`, `last_name`, `password`, `username`)
VALUES (2, 1, 'test.user@custodix.com', 1, 'Selenium', 0, 'TestUser', 'VdpuBew05SI=:2JIieKpnde8CJO23qnvQVw7BjJE=', 'Selenium TestUser');

-- Cohort study
INSERT INTO `cohort_study` (`id`,`creationDate`, `description`, `name`, `author_id`)
VALUES (1, CURDATE(), 'A prepopulated study for selenium testing', 'Selenium Prepopulated study', 2);

-- Cohort
INSERT INTO `cohort` (`id`, `creationDate`, `description`, `name`, `patientCount`, `repoKey`, `author_id`, `cstudyId`, `source_id`)
VALUES (1, CURDATE(), 'A prepopulated cohort for selenium testing', 'Selenium Prepopulated cohort', 159, 'SELENIUM-PREP', 2, 1, NULL);
