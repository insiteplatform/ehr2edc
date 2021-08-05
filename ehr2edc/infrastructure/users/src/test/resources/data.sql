INSERT INTO public.app_user (id, version, email, enabled, first_name, deleted, last_name, username) VALUES (1, 3, 'gert@trinetx.com', true, null, false, null, 'gert');
INSERT INTO public.app_user (id, version, email, enabled, first_name, deleted, last_name, username) VALUES (2, 3, 'gert-pending@trinetx.com', false, null, false, null, 'gert-pending');
INSERT INTO public.app_user (id, version, email, enabled, first_name, deleted, last_name, username) VALUES (3, 3, 'gert-deleted@trinetx.com', true, null, true, null, 'gert-deleted');

INSERT INTO public.app_user (id, version, email, enabled, first_name, deleted, last_name, username) VALUES (4, 3, 'gert-drm@trinetx.com', true, null, false, null, 'gert-drm');
INSERT INTO public.app_user (id, version, email, enabled, first_name, deleted, last_name, username) VALUES (5, 3, 'gert-no-drm@trinetx.com', true, null, false, null, 'gert-no-drm');
/* UserGroups */
INSERT INTO public.usergroup (id, version, name) VALUES (1, 1, 'Administrators');
INSERT INTO public.usergroup (id, version, name) VALUES (3, 1, 'Data Relationship Managers');
INSERT INTO public.usergroup (id, version, name) VALUES (4, 1, 'Users');
INSERT INTO public.usergroup (id, version, name) VALUES (5, 1, 'Power Users');

INSERT INTO public.usergroup_user (group_id, user_id) VALUES (3, 4);
INSERT INTO public.usergroup_user (group_id, user_id) VALUES (4, 5);