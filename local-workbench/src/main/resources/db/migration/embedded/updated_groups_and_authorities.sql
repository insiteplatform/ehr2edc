CREATE TABLE IF NOT EXISTS `usergroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_group_name` (`name`)
);

CREATE TABLE IF NOT EXISTS `authority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(80) NOT NULL,
  CONSTRAINT `PK_id` PRIMARY KEY (`id`),
  UNIQUE KEY `UK_authority_name` (`name`)
);

CREATE TABLE IF NOT EXISTS `usergroup_authority` (
  `group_id` bigint(20) NOT NULL,
  `authority_id` bigint(20) NOT NULL,
  CONSTRAINT `PK_groupId_authorityId` PRIMARY KEY (`group_id`,`authority_id`),
  CONSTRAINT `FK_usergroupAuthority_authority_id` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`id`),
  CONSTRAINT `FK_usergroupAuthority_usergroup_id` FOREIGN KEY (`group_id`) REFERENCES `usergroup` (`id`)
);

MERGE INTO `usergroup` (id, version, name)
VALUES 
(1, 1, 'Administrators'),(3, 1, 'Data Relationship Managers'),(4, 1, 'Users'),(5, 1, 'Power Users');

MERGE INTO `authority` (id, version, name)
VALUES
(1, 1, 'VIEW_ACCOUNTS'),
(2, 1, 'MANAGE_ACCOUNTS'),
(3, 1, 'VIEW_AUDIT_QUERIES'),
(4, 1, 'VIEW_PATIENT_FACTS'),
(5, 1, 'VIEW_SPONSOR_STUDIES'),
(6, 1, 'VIEW_ACTUATOR_DETAILS'),
(7, 1, 'MANAGE_PLACEMENT'),
(8, 1, 'CREATE_COHORTS'),
(9, 1, 'ASSIGN_INVESTIGATORS'),
(10, 1, 'ACCEPT_RECRUITMENT_STUDIES'),
(11, 1, 'DECLINE_RECRUITMENT_STUDIES'),
(12, 1, 'VIEW_RECRUITMENT_STUDIES'),
(13, 1, 'ARCHIVE_RECRUITMENT_STUDIES'),
(14, 1, 'EDIT_RECRUITMENT_STUDIES'),
(15, 1, 'EXPORT_COHORT_RECRUITMENT_STUDY');

MERGE INTO `usergroup_authority` (group_id, authority_id)
VALUES
(1, 1), (1, 2), (1, 6),
(3, 3), (3, 7), (3, 9), (3, 10), (3, 11), (3, 12), (3, 13), (3, 14),
(4, 5), (4, 8),
(5, 4);