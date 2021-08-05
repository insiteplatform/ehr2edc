package com.custodix.insite.local.ehr2edc.provenance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;

public class ProvenanceMedicationObjectMother {
	public static final String OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL = "26643006";
	public static final String OMEPRAZOLE_DOSE_FORM_CAPSULE = "420692007";
	public static final String OMEPRAZOLE_DOSING_FREQUENCY_DAILY = "229797004";
	public static final LocalDateTime OMEPRAZOLE_START_DATE = LocalDateTime.of(2019, 7, 8, 13, 35);
	public static final LocalDateTime OMEPRAZOLE_END_DATE = LocalDateTime.of(2019, 7, 9, 13, 35);
	public static final String OMEPRAZOLE_CODE = "A10A";
	public static final String OMEPRAZOLE_NAME = "omeprazole";
	public static final BigDecimal OMEPRAZOLE_DOSAGE_VALUE = BigDecimal.TEN;
	public static final String OMEPRAZOLE_DOSAGE_UNIT = "mg";
	public static final EventType OMEPRAZOLE_EVENT_TYPE = EventType.ADMINISTRATION;

	public static ProvenanceMedication omeprazole() {
		return ProvenanceMedication.newBuilder()
				.withConcept(MedicationConcept.newBuilder()
						.withConcept(ConceptCode.of(OMEPRAZOLE_CODE))
						.withName(OMEPRAZOLE_NAME)
						.build())
				.withStartDate(OMEPRAZOLE_START_DATE)
				.withEndDate(OMEPRAZOLE_END_DATE)
				.withDosage(Dosage.newBuilder()
						.withValue(OMEPRAZOLE_DOSAGE_VALUE)
						.withUnit(OMEPRAZOLE_DOSAGE_UNIT)
						.build())
				.withAdministrationRoute(OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL)
				.withDoseForm(OMEPRAZOLE_DOSE_FORM_CAPSULE)
				.withDosingFrequency(OMEPRAZOLE_DOSING_FREQUENCY_DAILY)
				.withEventType(OMEPRAZOLE_EVENT_TYPE)
				.build();
	}

	public static ProvenanceMedication empty() {
		return ProvenanceMedication.newBuilder()
				.build();
	}
}
