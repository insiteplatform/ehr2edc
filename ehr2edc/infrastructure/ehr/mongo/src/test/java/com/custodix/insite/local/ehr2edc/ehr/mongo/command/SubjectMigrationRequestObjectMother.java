package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectMigrationRequestObjectMother {

	private static final String STUDY_ID = "studyId-123";
	private static final String PATIENT_ID = "patientId-456";
	private static final String PATIENT_ID_SOURCE = "patientIdSource";
	private static final String SUBJECT_ID = "subjectId-123";

	public static SubjectMigration.Request.Builder aDefaultSubjectMigrationRequestBuilder() {
		return SubjectMigration.Request.newBuilder()
				.withStudyId(StudyId.of(STUDY_ID))
				.withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
				.withSubjectId(SubjectId.of(SUBJECT_ID));
	}
}
