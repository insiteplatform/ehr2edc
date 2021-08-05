package com.custodix.insite.mongodb.export.patient.application.api;

import static com.custodix.insite.mongodb.vocabulary.PatientIdentifierObjectMother.aDefaultPatientIdentifier;

public class ActivateSubjectRequestObjectMother {

	public static ActivateSubject.Request.Builder aDefaultActivateSubjectRequestBuilder() {
		return ActivateSubject.Request.newBuilder()
				.withPatientIdentifier(aDefaultPatientIdentifier());
	}

}