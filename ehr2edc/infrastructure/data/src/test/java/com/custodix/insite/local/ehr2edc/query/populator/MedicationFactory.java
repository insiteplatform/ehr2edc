package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.time.LocalDateTime;

public class MedicationFactory {
	private static final ConceptCode CONCEPT_OMEPRAZOLE = ConceptCode.conceptFor("A10A");

	Medication aOmeprazoleMedication(SubjectId subjectId, LocalDateTime date) {
		MedicationConcept medication = MedicationConcept.newBuilder()
				.withConcept(CONCEPT_OMEPRAZOLE)
				.withName("omeprazole")
				.build();

		return Medication.newBuilder()
				.withSubjectId(subjectId)
				.withStartDate(date)
				.withEndDate(date)
				.withConcept(medication)
				.build();
	}

}
