INSERT INTO authority (id, version, name)
VALUES
(29, 1, 'ActuatorDetails_View');

INSERT INTO usergroup_authority (group_id, authority_id)
SELECT groups.id, 29
FROM usergroup groups
WHERE groups.name = 'Administrators'