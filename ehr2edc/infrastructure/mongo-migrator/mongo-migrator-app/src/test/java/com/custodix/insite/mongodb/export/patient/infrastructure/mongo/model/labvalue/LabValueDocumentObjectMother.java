package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

import static com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabConceptObjectMother.aDefaultLabConcept;
import static com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueInterpretationObjectMother.aDefaultLabValueInterpretation;

import java.time.LocalDateTime;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class LabValueDocumentObjectMother {

	private static final LocalDateTime NOW = LocalDateTime.now();
	private static final LocalDateTime YESTERDAY = NOW.minusDays(1);

	public static LabValueDocument.Builder aDefaultLabValueDocumentBuilder() {
		return LabValueDocument.newBuilder()
				.forSubject(SubjectId.of("LabValueDocument.SubjectId"))
				.withStartDate(YESTERDAY)
				.withEndDate(NOW)
				.withLabConcept(aDefaultLabConcept())
				.withVendor("vendor")
				.withQualitativeResult(aDefaultLabValueInterpretation())
				.withQuantitativeResult(LabMeasurementObjectMother.aDefaultLabMeasurement());
	}

}