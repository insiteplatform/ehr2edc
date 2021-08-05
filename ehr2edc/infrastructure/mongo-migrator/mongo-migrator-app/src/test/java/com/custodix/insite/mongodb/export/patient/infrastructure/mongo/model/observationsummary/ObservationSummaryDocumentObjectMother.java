package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary;

import java.time.LocalDate;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class ObservationSummaryDocumentObjectMother {

	public static ObservationSummaryDocument aDefaultObservationSummary() {
		return aDefaultObservationSummaryBuilder().build();
	}

	public static ObservationSummaryDocument.Builder aDefaultObservationSummaryBuilder() {
		return ObservationSummaryDocument.newBuilder()
				.withSubjectId(SubjectId.of("subjectId"))
				.withCategory("category")
				.withDate(LocalDate.now())
				.withAmountOfObservations(10);
	}
}