INSERT INTO authority (id, version, name)
VALUES
(31, 1, 'Recruitment_Manage');

INSERT INTO usergroup_authority (group_id, authority_id)
SELECT groups.id, 31
FROM usergroup groups
WHERE groups.name = 'Data Relationship Managers';