package com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway;

import static java.util.Optional.ofNullable;

import java.util.function.Function;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.DosageField;
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationConceptField;
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument;

public class ToMedication implements Function<MedicationDocument, Medication> {
	@Override
	public Medication apply(final MedicationDocument document) {
		Medication.Builder builder = Medication.newBuilder()
				.withSubjectId(document.getSubjectId())
				.withEndDate(document.getEndDate())
				.withStartDate(document.getStartDate())
				.withAdministrationRoute(document.getRoute())
				.withDoseForm(document.getDoseFormat())
				.withDosingFrequency(document.getFrequency())
				.withEventType(document.getEventType());

		ofNullable(document.getConcept()).map(this::mapConcept)
				.ifPresent(builder::withConcept);

		ofNullable(document.getDosage()).map(this::mapDosage)
				.ifPresent(builder::withDosage);

		return builder.build();
	}

	private Dosage mapDosage(DosageField dosageField) {
		return Dosage.newBuilder()
				.withUnit(dosageField.getUnit())
				.withValue(dosageField.getValue())
				.build();
	}

	private MedicationConcept mapConcept(final MedicationConceptField medicationConceptField) {
		return MedicationConcept.newBuilder()
				.withConcept(medicationConceptField.getConcept())
				.withName(medicationConceptField.getName())
				.build();
	}
}
