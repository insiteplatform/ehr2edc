package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class MedicationDocumentObjectMother {

	protected static final LocalDateTime NOW = LocalDateTime.now();
	protected static final LocalDateTime YESTERDAY = NOW.minusDays(1);

	public static MedicationDocument.Builder aDefaultMedicationDocumentBuilder() {
		return MedicationDocument.newBuilder()
				.withConcept(MedicationConceptObjectMother.aDefaultMedicationConcept())
				.withDosage(new Dosage(BigDecimal.TEN, "kg"))
				.withDoseFormat("doseFormat")
				.withStartDate(YESTERDAY)
				.withEndDate(NOW)
				.withFrequency("frequency")
				.withRoute("route")
				.withSubjectId(SubjectId.of("subjectId"));
	}

}