INSERT INTO usergroup_user (group_id, user_id)
SELECT regular_user.id, au.id FROM app_user au, (SELECT groups.id FROM usergroup groups WHERE groups.name = 'Users') regular_user
WHERE NOT EXISTS (SELECT 1 FROM usergroup_user uu WHERE uu.group_id = regular_user.id AND uu.user_id = au.id);