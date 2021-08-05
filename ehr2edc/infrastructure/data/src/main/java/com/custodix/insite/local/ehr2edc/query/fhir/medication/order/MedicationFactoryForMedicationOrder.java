package com.custodix.insite.local.ehr2edc.query.fhir.medication.order;

import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.MedicationConceptCodeProcessor;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;

public class MedicationFactoryForMedicationOrder {

	private final MedicationConceptCodeProcessor medicationConceptCodeProcessor;

	public MedicationFactoryForMedicationOrder(final MedicationConceptCodeProcessor medicationConceptCodeProcessor) {
		this.medicationConceptCodeProcessor = medicationConceptCodeProcessor;
	}

	Stream<Medication> create(final MedicationOrder order, final SubjectId subjectId) {
		return order.getDosageInstruction().stream()
				.filter(this::hasDosage)
				.map(dosageInstruction -> createMedication(order, subjectId, dosageInstruction));
	}

	private Medication createMedication(final MedicationOrder order, final SubjectId subjectId,
			final MedicationOrder.DosageInstruction dosageInstruction) {

		final Medication.Builder medicationBuilder = Medication.newBuilder();
		medicationBuilder.withEventType(EventType.PRESCRIPTION);
		medicationBuilder.withSubjectId(subjectId);

		final FhirMedicationOrderMapper mapper =
				new FhirMedicationOrderMapper(medicationConceptCodeProcessor, order, dosageInstruction);
		medicationBuilder.withDosage(mapper.getDosage());
		mapper.getRoute().ifPresent(medicationBuilder::withAdministrationRoute);
		mapper.getDosageFrequency().ifPresent(medicationBuilder::withDosingFrequency);
		mapper.getConcept().ifPresent(medicationBuilder::withConcept);
		mapper.getStartDate().ifPresent(medicationBuilder::withStartDate);
		mapper.getEndDate().ifPresent(medicationBuilder::withEndDate);
		mapper.getDosageForm().ifPresent(medicationBuilder::withDoseForm);
		return medicationBuilder.build();
	}

	private boolean hasDosage(final MedicationOrder.DosageInstruction dosageInstruction) {
		return dosageInstruction.getDose() != null;
	}
}
