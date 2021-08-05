package com.custodix.insite.mongodb.export.patient.domain.model;

import java.time.Instant;

public interface SummarizableObservationFact {
	Instant getObservationInstant();

	String getCategory();

	String getSubjectIdentifier();
}