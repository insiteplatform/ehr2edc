package com.custodix.insite.local.ehr2edc.query.mongo.medication.model;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicationDocumentObjectMother {
	public static final String OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL = "26643006";
	public static final String OMEPRAZOLE_DOSE_FORM_CAPSULE = "420692007";
	public static final String OMEPRAZOLE_DOSING_FREQUENCY_DAILY = "229797004";
	public static final LocalDateTime OMEPRAZOLE_START_DATE = LocalDateTime.now();
	public static final LocalDateTime OMEPRAZOLE_END_DATE = LocalDateTime.now();
	public static final BigDecimal OMEPRAZOLE_DOSAGE_VALUE = new BigDecimal("100");
	public static final String OMEPRAZOLE_DOSAGE_UNIT = "mg";
	public static final EventType OMEPRAZOLE_EVENT_TYPE = EventType.ADMINISTRATION;

	public static MedicationDocument aDefaultMedicationDocument() {
		return MedicationDocument.newBuilder()
				.withSubjectId(SubjectId.of("MY_SUBJECT_ID"))
				.withStartDate(LocalDateTime.now())
				.withEndDate(LocalDateTime.now())
				.withConcept(MedicationConceptFieldObjectMother.aDefaultConcept())
				.build();
	}

	public static MedicationDocument anOmeprazoleMedicationDocument() {
		return anOmeprazoleMedicationDocumentBuilder().build();
	}

	public static MedicationDocument.Builder anOmeprazoleMedicationDocumentBuilder() {
		return MedicationDocument.newBuilder()
				.withSubjectId(SubjectId.of("MY_SUBJECT_ID"))
				.withStartDate(OMEPRAZOLE_START_DATE)
				.withEndDate(OMEPRAZOLE_END_DATE)
				.withConcept(MedicationConceptFieldObjectMother.anOmeprazoleConcept())
				.withDosage(new DosageField(OMEPRAZOLE_DOSAGE_VALUE, OMEPRAZOLE_DOSAGE_UNIT))
				.withRoute(OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL)
				.withDoseFormat(OMEPRAZOLE_DOSE_FORM_CAPSULE)
				.withFrequency(OMEPRAZOLE_DOSING_FREQUENCY_DAILY)
				.withEventType(OMEPRAZOLE_EVENT_TYPE);
	}

}