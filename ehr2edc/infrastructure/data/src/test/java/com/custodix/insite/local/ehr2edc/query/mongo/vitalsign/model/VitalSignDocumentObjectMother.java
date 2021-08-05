package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.time.LocalDateTime;

public class VitalSignDocumentObjectMother {
	public static final LocalDateTime BMI_EFFECTIVE_DATE = LocalDateTime.now();

	public static VitalSignDocument aDefaultVitalSignDocument() {
		return VitalSignDocument.newBuilder()
				.withSubjectId(SubjectId.of("MY_SUBJECT_ID"))
				.withEffectiveDateTime(LocalDateTime.now())
				.withMeasurement(VitalSignMeasurementFieldObjectMother.aDefaultVitalSignMeasurementField())
				.withConcept(VitalSignConceptFieldObjectMother.aDefaultConcept())
				.build();
	}

	public static VitalSignDocument aNormalAdultBmiVitalSignDocument() {
		return aNormalAdultBmiVitalSignDocumentBuilder().build();
	}

	public static VitalSignDocument.Builder aNormalAdultBmiVitalSignDocumentBuilder() {
		return VitalSignDocument.newBuilder()
				.withSubjectId(SubjectId.of("MY_SUBJECT_ID"))
				.withEffectiveDateTime(BMI_EFFECTIVE_DATE)
				.withMeasurement(VitalSignMeasurementFieldObjectMother.aNormalAdultBmiVitalSignMeasurementField())
				.withConcept(VitalSignConceptFieldObjectMother.aBmiConcept());
	}

}