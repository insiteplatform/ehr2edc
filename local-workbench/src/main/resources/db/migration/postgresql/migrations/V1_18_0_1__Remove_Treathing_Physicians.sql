DELETE FROM usergroup_user
	WHERE group_id = (SELECT id FROM usergroup WHERE name = 'Treating Physicians');

DELETE FROM usergroup_authority
	WHERE group_id = (SELECT id FROM usergroup WHERE name = 'Treating Physicians');

DELETE FROM usergroup
	WHERE name = 'Treating Physicians';