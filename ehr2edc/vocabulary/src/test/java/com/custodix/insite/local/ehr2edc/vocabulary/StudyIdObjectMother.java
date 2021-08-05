package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public class StudyIdObjectMother {

	public static StudyId aRandomStudyId() {
		return StudyId.of(UUID.randomUUID().toString());
	}
}