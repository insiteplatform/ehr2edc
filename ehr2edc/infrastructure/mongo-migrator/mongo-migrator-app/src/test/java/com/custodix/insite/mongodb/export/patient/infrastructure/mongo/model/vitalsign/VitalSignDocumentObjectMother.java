package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign;

import java.time.LocalDateTime;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class VitalSignDocumentObjectMother {

	private static final LocalDateTime NOW = LocalDateTime.now();

	public static VitalSignDocument.Builder aDefaultVitalSignDocumentBuilder() {
		return VitalSignDocument.newBuilder()
				.withSubjectId(SubjectId.of("subjectId"))
				.withMeasurement(MeasurementObjectMother.aDefaultMeasurement())
				.withEffectiveDateTime(NOW)
				.withConcept(VitalSignConceptObjectMother.aDefaultVitalConcept());
	}

}