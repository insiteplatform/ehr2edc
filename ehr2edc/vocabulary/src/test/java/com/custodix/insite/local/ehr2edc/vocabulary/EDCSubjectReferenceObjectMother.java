package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public class EDCSubjectReferenceObjectMother {

	public static EDCSubjectReference aRandomEdcSubjectReference() {
		return EDCSubjectReference.of(UUID.randomUUID().toString());
	}
}