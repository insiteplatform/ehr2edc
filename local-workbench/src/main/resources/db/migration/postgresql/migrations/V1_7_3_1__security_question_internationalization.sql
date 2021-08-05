
DROP TABLE IF EXISTS SECURITY_QUESTION_MIGRATE_TMP;

CREATE TABLE SECURITY_QUESTION_MIGRATE_TMP
 (
	id varchar(255),
	text varchar(512)
 );

INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('1','What was your childhood nickname?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('2','What is the name of your favorite childhood friend?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('3','In what city or town did your mother and father meet?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('4','What is the middle name of your oldest child?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('5','What is your favorite team?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('6','What is your favorite movie?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('7','What was your favorite sport in high school?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('8','What was your favorite food as a child?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('9','What is the first name of the boy or girl that you first kissed?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('10','What was the make and model of your first car?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('11','What was the name of the hospital where you were born?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('12','Who is your childhood sports hero?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('13','What school did you attend for sixth grade?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('14','What was the last name of your third grade teacher?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('15','In what town was your first job?');
INSERT INTO SECURITY_QUESTION_MIGRATE_TMP values ('16','What was the name of the company where you had your first job?');

UPDATE app_user SET securityQuestion = SECURITY_QUESTION_MIGRATE_TMP.id
  from SECURITY_QUESTION_MIGRATE_TMP
  where app_user.securityQuestion = SECURITY_QUESTION_MIGRATE_TMP.text;

DROP TABLE IF EXISTS SECURITY_QUESTION_MIGRATE_TMP;

ALTER TABLE app_user RENAME securityQuestion TO securityQuestionId;