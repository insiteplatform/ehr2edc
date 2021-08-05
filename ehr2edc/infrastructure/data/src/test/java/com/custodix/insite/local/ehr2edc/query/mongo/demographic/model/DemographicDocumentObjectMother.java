package com.custodix.insite.local.ehr2edc.query.mongo.demographic.model;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class DemographicDocumentObjectMother {
	public static final String GENDER_FEMALE = "female";

	public static DemographicDocument.Builder genderFemaleBuilder() {
		return DemographicDocument.newBuilder()
				.withSubjectId(SubjectId.of("MY_SUBJECT_ID"))
				.withDemographicType(DemographicType.GENDER)
				.withValue(GENDER_FEMALE);
	}

}
