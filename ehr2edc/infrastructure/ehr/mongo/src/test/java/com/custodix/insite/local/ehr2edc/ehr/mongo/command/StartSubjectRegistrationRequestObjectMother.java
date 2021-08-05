package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class StartSubjectRegistrationRequestObjectMother {

	private static final String PATIENT_ID = "patientId-456";
	private static final String PATIENT_ID_SOURCE = "patientIdSource";
	private static final String SUBJECT_ID = "subjectId-123";

	public static StartSubjectRegistration.Request.Builder aDefaultStartSubjectRegistrationRequestBuilder() {
		return StartSubjectRegistration.Request.newBuilder()
				.withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build())
				.withSubjectId(SubjectId.of(SUBJECT_ID));
	}
}
