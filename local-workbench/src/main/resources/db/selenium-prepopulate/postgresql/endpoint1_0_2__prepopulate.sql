INSERT INTO QT_QUERY_MASTER(QUERY_MASTER_ID, NAME, USER_ID, GROUP_ID, MASTER_TYPE_CD, PLUGIN_ID, CREATE_DATE, DELETE_DATE, DELETE_FLAG, REQUEST_XML, GENERATED_SQL, I2B2_REQUEST_XML, PM_XML)
VALUES (1, 'A PREPOPULATED COHORT FOR SELENIUM TESTING', 'TEST.USER@CUSTODIX.COM', 'SELENIUM USERS', NULL, NULL, CURRENT_DATE, NULL, 'N', NULL, '', NULL, NULL);
SELECT setval(pg_get_serial_sequence('qt_query_master', 'query_master_id'), 2);

INSERT INTO QT_QUERY_INSTANCE(QUERY_INSTANCE_ID, QUERY_MASTER_ID, USER_ID, GROUP_ID, BATCH_MODE, START_DATE, END_DATE, DELETE_FLAG, STATUS_TYPE_ID, MESSAGE)
    VALUES (1, 1, 'TEST.USER@CUSTODIX.COM', 'SELENIUM USERS', NULL, CURRENT_DATE, CURRENT_DATE, 'N', 6, 'A PREPOPULATED COHORT FOR SELENIUM TESTING');
SELECT setval(pg_get_serial_sequence('qt_query_instance', 'query_instance_id'), 2);
	
INSERT INTO QT_QUERY_RESULT_INSTANCE(RESULT_INSTANCE_ID, QUERY_INSTANCE_ID, RESULT_TYPE_ID, SET_SIZE, START_DATE, END_DATE, STATUS_TYPE_ID, DELETE_FLAG, MESSAGE, DESCRIPTION, REAL_SET_SIZE, OBFUSC_METHOD)
    VALUES (1, 1, 3, 159, CURRENT_DATE, CURRENT_DATE, 6, 'N', 'NUMBER OF PATIENTS FOR "SELENIUM COHORT"', NULL, 159, NULL);
SELECT setval(pg_get_serial_sequence('qt_query_result_instance', 'result_instance_id'), 2);
	
INSERT INTO QT_PATIENT_SET_COLLECTION(RESULT_INSTANCE_ID, SET_INDEX, PATIENT_NUM)
    VALUES (1, 1, 1),(1, 2, 2),(1, 3, 3),(1, 4, 5),(1, 5, 6),(1, 6, 8),(1, 7, 9),
			(1, 8, 10),(1, 9, 11),(1, 10, 12),(1, 11, 13),(1, 12, 15),(1, 13, 16),(1, 14, 18),(1, 15, 19),
			(1, 16, 20),(1, 17, 21),(1, 18, 22),(1, 19, 23),(1, 20, 25),(1, 21, 26),(1, 22, 28),(1, 23, 29),
			(1, 24, 30),(1, 25, 31),(1, 26, 32),(1, 27, 33),(1, 28, 35),(1, 29, 36),(1, 30, 38),(1, 31, 39),
			(1, 32, 40),(1, 33, 41),(1, 34, 42),(1, 35, 43),(1, 36, 45),(1, 37, 46),(1, 38, 48),(1, 39, 49),
			(1, 40, 50),(1, 41, 51),(1, 42, 52),(1, 43, 53),(1, 44, 55),(1, 45, 56),(1, 46, 58),(1, 47, 59),
			(1, 48, 60),(1, 49, 61),(1, 50, 62),(1, 51, 63),(1, 52, 65),(1, 53, 66),(1, 54, 68),(1, 55, 69),
			(1, 56, 70),(1, 57, 71),(1, 58, 72),(1, 59, 73),(1, 60, 75),(1, 61, 76),(1, 62, 78),(1, 63, 79),
			(1, 64, 80),(1, 65, 81),(1, 66, 82),(1, 67, 83),(1, 68, 85),(1, 69, 86),(1, 70, 88),(1, 71, 89),
			(1, 72, 90),(1, 73, 91),(1, 74, 92),(1, 75, 93),(1, 76, 95),(1, 77, 96),(1, 78, 98),(1, 79, 99),
			(1, 80, 100),(1, 81, 101),(1, 82, 102),(1, 83, 103),(1, 84, 105),(1, 85, 106),(1, 86, 108),(1, 87, 109),
			(1, 88, 110),(1, 89, 111),(1, 90, 112),(1, 91, 113),(1, 92, 115),(1, 93, 116),(1, 94, 118),(1, 95, 119),
			(1, 96, 120),(1, 97, 121),(1, 98, 122),(1, 99, 123),(1, 100, 125),(1, 101, 126),(1, 102, 128),(1, 103, 129),
			(1, 104, 130),(1, 105, 131),(1, 106, 132),(1, 107, 133),(1, 108, 135),(1, 109, 136),(1, 110, 138),(1, 111, 139),
			(1, 112, 140),(1, 113, 141),(1, 114, 142),(1, 115, 143),(1, 116, 145),(1, 117, 146),(1, 118, 148),(1, 119, 149),
			(1, 120, 150),(1, 121, 151),(1, 122, 152),(1, 123, 153),(1, 124, 155),(1, 125, 156),(1, 126, 158),(1, 127, 159),
			(1, 128, 160),(1, 129, 161),(1, 130, 162),(1, 131, 163),(1, 132, 165),(1, 133, 166),(1, 134, 168),(1, 135, 169),
			(1, 136, 170),(1, 137, 171),(1, 138, 172),(1, 139, 173),(1, 140, 175),(1, 141, 176),(1, 142, 178),(1, 143, 179),
			(1, 144, 180),(1, 145, 181),(1, 146, 182),(1, 147, 183),(1, 148, 185),(1, 149, 186),(1, 150, 188),(1, 151, 189),
			(1, 152, 190),(1, 153, 191),(1, 154, 192),(1, 155, 193),(1, 156, 195),(1, 157, 196),(1, 158, 198),(1, 159, 199);
SELECT setval(pg_get_serial_sequence('qt_patient_set_collection', 'patient_set_coll_id'), 160);
			
INSERT INTO INSITE_QUERY_ID_MAPPING(QUERY_MASTER_ID, QUERY_GUID, STUDY_ID)
    VALUES (1, '4B0EC08F-E399-42E1-B708-02129F2A21E4', 'SELENIUM-PREP');
SELECT setval(pg_get_serial_sequence('insite_query_id_mapping', 'query_master_id'), 2);
