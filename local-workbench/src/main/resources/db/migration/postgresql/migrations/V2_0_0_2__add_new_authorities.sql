DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'MANAGE_RECRUITMENT');
DELETE FROM authority WHERE name = 'MANAGE_RECRUITMENT';

INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'ASSIGN_INVESTIGATORS');
INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'ACCEPT_RECRUITMENT_STUDIES');
INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'DECLINE_RECRUITMENT_STUDIES');
INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'VIEW_RECRUITMENT_STUDIES');
INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'ARCHIVE_RECRUITMENT_STUDIES');
INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'EDIT_RECRUITMENT_STUDIES');

INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'ASSIGN_INVESTIGATORS'));
INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'ACCEPT_RECRUITMENT_STUDIES'));
INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'DECLINE_RECRUITMENT_STUDIES'));
INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'VIEW_RECRUITMENT_STUDIES'));
INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'ARCHIVE_RECRUITMENT_STUDIES'));
INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Data Relationship Managers'), (SELECT id FROM authority where name = 'EDIT_RECRUITMENT_STUDIES'));