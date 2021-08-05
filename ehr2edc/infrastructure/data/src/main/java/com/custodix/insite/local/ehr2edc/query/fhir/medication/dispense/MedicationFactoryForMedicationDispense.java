package com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense;

import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.*;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;

class MedicationFactoryForMedicationDispense {

	Stream<Medication> create(MedicationDispense medicationDispense, SubjectId subjectId) {
		 return medicationDispense.getDosageInstruction().stream()
				.map(this::createMedicationBuilder)
				.map(MedicationBuilderWrapper::addDosage)
				.map(MedicationBuilderWrapper::addRoute)
				.map(MedicationBuilderWrapper::addDosageFrequency)
				.map(medicationBuilder -> medicationBuilder.addConcept(medicationDispense))
				.map(medicationBuilder -> medicationBuilder.addSubjectId(subjectId))
				.map(medicationBuilder -> medicationBuilder.addStartDate(medicationDispense))
				.map(medicationBuilder -> medicationBuilder.addEndDate(medicationDispense))
				.map(medicationBuilder -> medicationBuilder.addDosageForm(medicationDispense))
				.map(MedicationBuilderWrapper::build);
	}

	private MedicationBuilderWrapper createMedicationBuilder(MedicationDispense.DosageInstruction dosageInstruction) {
		return new MedicationBuilderWrapper(dosageInstruction);
	}

	private final class MedicationBuilderWrapper {
		private final MedicationDispense.DosageInstruction  dosageInstruction;
		private final Medication.Builder medicationBuilder;

		private MedicationBuilderWrapper(MedicationDispense.DosageInstruction  dosageInstruction) {
			this.dosageInstruction = dosageInstruction;
			this.medicationBuilder = Medication.newBuilder();
			addMedicationType();
		}

		public Medication build() {
			return medicationBuilder.build();
		}

		private MedicationBuilderWrapper addDosageFrequency() {
			Optional.ofNullable(dosageInstruction.getTiming())
					.map(TimingDt::getRepeat)
					.map(FhirDstu2FrequencyConverter::new)
					.map(FhirDstu2FrequencyConverter::getFrequency)
					.ifPresent(medicationBuilder::withDosingFrequency);
			return this;
		}

		private void addMedicationType() {
			medicationBuilder.withEventType(EventType.DISPENSION);
		}

		private MedicationBuilderWrapper addDosage() {
			Dosage.Builder dosageBuilder = Dosage.newBuilder();
			IDatatype dosage = dosageInstruction.getDose();
			new DosageValueProcessor().execute(dosage, dosageBuilder::withValue);
			new DosageUnitProcessor().execute(dosage, dosageBuilder::withUnit);
			medicationBuilder.withDosage(dosageBuilder.build());
			return this;
		}

		private MedicationBuilderWrapper addRoute() {
			if (hasDosageRoute()) {
				medicationBuilder.withAdministrationRoute(dosageInstruction.getRoute().getCoding().get(0).getCode());
			}
			return this;
		}

		private boolean hasDosageRoute() {
			return dosageInstruction.getRoute() != null &&
					dosageInstruction.getRoute().getCoding() != null &&
					!dosageInstruction.getRoute().getCoding().isEmpty();
		}

		private MedicationBuilderWrapper addConcept(MedicationDispense medicationDispense) {
			new MedicationConceptCodeProcessor().execute(medicationDispense.getMedication(), medicationBuilder::withConcept);
			return this;
		}

		private MedicationBuilderWrapper addSubjectId(SubjectId subjectId) {
			medicationBuilder.withSubjectId(subjectId);
			return this;
		}

		private MedicationBuilderWrapper addStartDate(MedicationDispense medicationDispense) {
			new StartDateProcessor().execute(dosageInstruction.getTiming(), medicationDispense, medicationBuilder::withStartDate);
			return this;
		}

		private MedicationBuilderWrapper addEndDate(MedicationDispense medicationDispense) {
			new EndDateProcessor().execute(dosageInstruction.getTiming(), medicationDispense, medicationBuilder::withEndDate);
			return this;
		}

		private MedicationBuilderWrapper addDosageForm(MedicationDispense medicationDispense) {
			new MedicationDoseFormProcessor().execute(medicationDispense.getMedication(), medicationBuilder::withDoseForm);
			return this;
		}
	}
}
