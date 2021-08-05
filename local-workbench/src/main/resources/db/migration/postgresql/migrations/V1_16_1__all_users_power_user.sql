INSERT INTO usergroup_user (group_id, user_id)
SELECT 5, au.id FROM app_user au
WHERE NOT EXISTS (SELECT 1 FROM usergroup_user uu WHERE uu.group_id = 5 AND uu.user_id = au.id);