package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public class PatientIdObjectMother {

	public static PatientCDWReference aRandomPatientId() {
		return PatientCDWReference.newBuilder()
				.withSource(UUID.randomUUID().toString())
				.withId(UUID.randomUUID().toString())
				.build();
	}
}