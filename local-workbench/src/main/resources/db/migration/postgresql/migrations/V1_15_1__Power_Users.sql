INSERT INTO usergroup (id, version, name)
VALUES
(5, 1, 'Power Users');

INSERT INTO authority (id, version, name)
VALUES
(27, 1, 'Patient_List_Access');

INSERT INTO usergroup_authority (group_id, authority_id)
VALUES
(5, 27);