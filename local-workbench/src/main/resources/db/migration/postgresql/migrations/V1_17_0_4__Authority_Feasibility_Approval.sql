INSERT INTO authority (id, version, name)
VALUES
(30, 1, 'Feasibility_Approval');

INSERT INTO usergroup_authority (group_id, authority_id)
SELECT groups.id, 30
FROM usergroup groups
WHERE groups.name = 'Data Relationship Managers';