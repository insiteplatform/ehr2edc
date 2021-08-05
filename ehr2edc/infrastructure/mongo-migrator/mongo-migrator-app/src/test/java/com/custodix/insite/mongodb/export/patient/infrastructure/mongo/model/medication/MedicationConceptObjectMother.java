package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;

public class MedicationConceptObjectMother {
	public static MedicationConcept aDefaultMedicationConcept() {
		return aDefaultMedicationConceptBuilder().build();
	}

	public static MedicationConcept.Builder aDefaultMedicationConceptBuilder() {
		return MedicationConcept.newBuilder().withConcept(ConceptCode.conceptFor("concept")).withName("name");
	}
}
