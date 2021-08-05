package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public class SubjectIdObjectMother {

	public static SubjectId aRandomSubjectId() {
		return SubjectId.of(UUID.randomUUID().toString());
	}
}