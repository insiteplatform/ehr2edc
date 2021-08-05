package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.UUID;

public class ExternalSiteIdObjectMother {

	public static ExternalSiteId aRandomExternalSiteId() {
		return ExternalSiteId.of(UUID.randomUUID().toString());
	}

}