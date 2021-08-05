INSERT INTO authority (id, version, name)
VALUES
(28, 1, 'Feasibility_View');

INSERT INTO usergroup_authority (group_id, authority_id)
SELECT groups.id, 28
FROM usergroup groups
WHERE groups.name = 'Users'