package com.custodix.insite.mongodb.export.patient.domain.model.demographic;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface DemographicFact {

	String factType();

	String factValue();

	default String toDemographicDocumentFor(SubjectId subjectId) {
		return String.format("{\"subjectId\": \"%s\", \"demographicType\": \"%s\", \"value\": \"%s\"}", subjectId.getId(),
				factType(), factValue());
	}

}
