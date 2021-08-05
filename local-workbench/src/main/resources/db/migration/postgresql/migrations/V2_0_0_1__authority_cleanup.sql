DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'ManageAccount_Edit');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'ManageAccount_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Studies_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Studies_ViewAsPI');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Studies_ViewAsTP');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Metadata_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Query_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Protocole_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Results_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patients_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patients_Import');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patients_Update');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_PI_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_PI_Edit');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_TP_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_TP_Edit');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_AcceptDecline');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patients_ViewAsPI');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patients_ViewAsTP');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patient_View');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patient_ViewAsPI');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Study_Patient_ViewAsTP');
DELETE FROM usergroup_authority WHERE authority_id = (SELECT id FROM authority WHERE name = 'Feasibility');

UPDATE authority SET name = 'VIEW_ACCOUNTS' WHERE name = 'ManageUsers_View';
UPDATE authority SET name = 'MANAGE_ACCOUNTS' WHERE name = 'ManageUsers_Edit';
UPDATE authority SET name = 'VIEW_AUDIT_QUERIES' WHERE name = 'Studies_ViewAsDRM';
UPDATE authority SET name = 'VIEW_PATIENT_FACTS' WHERE name = 'Patient_List_Access';
UPDATE authority SET name = 'VIEW_SPONSOR_STUDIES' WHERE name = 'Feasibility_View';
UPDATE authority SET name = 'VIEW_ACTUATOR_DETAILS' WHERE name = 'ActuatorDetails_View';
UPDATE authority SET name = 'MANAGE_PLACEMENT' WHERE name = 'Feasibility_Approval';
UPDATE authority SET name = 'MANAGE_RECRUITMENT' WHERE name = 'Recruitment_Manage';

DELETE FROM authority WHERE name = 'ManageAccount_View';
DELETE FROM authority WHERE name = 'ManageAccount_Edit';
DELETE FROM authority WHERE name = 'Studies_View';
DELETE FROM authority WHERE name = 'Studies_ViewAsPI';
DELETE FROM authority WHERE name = 'Studies_ViewAsTP';
DELETE FROM authority WHERE name = 'Study_Metadata_View';
DELETE FROM authority WHERE name = 'Study_Query_View';
DELETE FROM authority WHERE name = 'Study_Protocole_View';
DELETE FROM authority WHERE name = 'Study_Results_View';
DELETE FROM authority WHERE name = 'Study_Patients_View';
DELETE FROM authority WHERE name = 'Study_Patients_Import';
DELETE FROM authority WHERE name = 'Study_Patients_Update';
DELETE FROM authority WHERE name = 'Study_PI_View';
DELETE FROM authority WHERE name = 'Study_PI_Edit';
DELETE FROM authority WHERE name = 'Study_TP_View';
DELETE FROM authority WHERE name = 'Study_TP_Edit';
DELETE FROM authority WHERE name = 'Study_AcceptDecline';
DELETE FROM authority WHERE name = 'Study_Patients_ViewAsPI';
DELETE FROM authority WHERE name = 'Study_Patients_ViewAsTP';
DELETE FROM authority WHERE name = 'Study_Patient_View';
DELETE FROM authority WHERE name = 'Study_Patient_ViewAsPI';
DELETE FROM authority WHERE name = 'Study_Patient_ViewAsTP';
DELETE FROM authority WHERE name = 'Feasibility';

INSERT INTO authority (id, version, name)
VALUES ((SELECT id + 1 FROM authority WHERE id = (SELECT MAX(id) FROM authority)), 1, 'CREATE_COHORTS');

INSERT INTO usergroup_authority (group_id, authority_id)
VALUES ((SELECT id FROM usergroup WHERE name = 'Users'), (SELECT id FROM authority where name = 'CREATE_COHORTS'));