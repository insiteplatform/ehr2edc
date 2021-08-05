package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class DemographicFactory {
	Demographic aDemographic(SubjectId subjectId) {
		return Demographic.newBuilder()
				.withDemographicType(DemographicType.BIRTH_DATE)
				.withSubjectId(subjectId)
				.withValue("2000-01-01")
				.build();
	}
}
