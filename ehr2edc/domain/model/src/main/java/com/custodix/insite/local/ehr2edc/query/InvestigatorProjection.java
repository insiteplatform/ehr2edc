package com.custodix.insite.local.ehr2edc.query;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface InvestigatorProjection {

	UserIdentifier getInvestigatorId();

	String getName();
}
