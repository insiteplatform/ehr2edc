package com.custodix.insite.local.ehr2edc.query.mongo.medication.model;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

public class MedicationConceptFieldObjectMother {
	public static final String OMEPRAZOLE_CONCEPT_CODE = "A02BC01";
	public static final String OMEPRAZOLE_NAME = "omeprazole";

	public static MedicationConceptField anOmeprazoleConcept() {
		return MedicationConceptField.newBuilder()
				.withConcept(ConceptCode.conceptFor(OMEPRAZOLE_CONCEPT_CODE))
				.withName(OMEPRAZOLE_NAME)
				.build();
	}

	public static MedicationConceptField aDefaultConcept() {
		return MedicationConceptField.newBuilder()
				.withConcept(ConceptCode.conceptFor("conceptCode"))
				.withName("omeprazole")
				.build();
	}
}
