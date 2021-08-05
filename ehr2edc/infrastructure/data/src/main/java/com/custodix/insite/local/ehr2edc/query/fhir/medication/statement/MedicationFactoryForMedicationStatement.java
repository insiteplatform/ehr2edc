package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.*;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;

public class MedicationFactoryForMedicationStatement {

	Stream<Medication> create(MedicationStatement medicationStatement, SubjectId subjectId) {
		return medicationStatement.getDosage().stream()
				.map(this::createMedicationBuilder)
				.map(MedicationBuilderWrapper::addDosage)
				.map(MedicationBuilderWrapper::addRoute)
				.map(MedicationBuilderWrapper::addDosageFrequency)
				.map(medicationBuilder -> medicationBuilder.addConcept(medicationStatement))
				.map(medicationBuilder -> medicationBuilder.addStartDate(medicationStatement))
				.map(medicationBuilder -> medicationBuilder.addEndDate(medicationStatement))
				.map(medicationBuilder -> medicationBuilder.addDosageForm(medicationStatement))
				.map(medicationBuilder -> medicationBuilder.addSubjectId(subjectId))
				.map(MedicationBuilderWrapper::build);
	}

	private MedicationBuilderWrapper createMedicationBuilder(MedicationStatement.Dosage dosage) {
		return new MedicationBuilderWrapper(dosage);
	}

	private final class MedicationBuilderWrapper {

		private final MedicationStatement.Dosage dosage;
		private final Medication.Builder medicationBuilder;

		private MedicationBuilderWrapper(MedicationStatement.Dosage dosage) {
			this.dosage = dosage;
			this.medicationBuilder = Medication.newBuilder();
			addMedicationType();
		}

		public MedicationBuilderWrapper addEndDate(MedicationStatement medicationStatement) {
			new EndDateProcessor().execute(medicationStatement, medicationBuilder::withEndDate);
			return this;
		}

		private MedicationBuilderWrapper addStartDate(MedicationStatement medicationStatement) {
			new StartDateProcessor().execute(medicationStatement, medicationBuilder::withStartDate);
			return this;
		}

		private MedicationBuilderWrapper addDosage() {
			Dosage.Builder dosageBuilder = Dosage.newBuilder();
			new DosageValueProcessor().execute(dosage.getQuantity(), dosageBuilder::withValue);
			new DosageUnitProcessor().execute(dosage.getQuantity(), dosageBuilder::withUnit);
			medicationBuilder.withDosage(dosageBuilder.build());
			return this;
		}

		private MedicationBuilderWrapper addRoute() {
			if (hasDosageRoute()) {
				medicationBuilder.withAdministrationRoute(dosage.getRoute().getCoding().get(0).getCode());
			}
			return this;
		}

		private boolean hasDosageRoute() {
			return dosage.getRoute() != null &&
					dosage.getRoute().getCoding() != null &&
					!dosage.getRoute().getCoding().isEmpty();
		}

		private MedicationBuilderWrapper addDosageFrequency() {
			Optional.ofNullable(dosage.getTiming())
						.map(TimingDt::getRepeat)
						.map(FhirDstu2FrequencyConverter::new)
						.map(FhirDstu2FrequencyConverter::getFrequency)
						.ifPresent(medicationBuilder::withDosingFrequency);
			return this;
		}

		private MedicationBuilderWrapper addConcept(MedicationStatement medicationStatement) {
			new MedicationConceptCodeProcessor().execute(medicationStatement.getMedication(), medicationBuilder::withConcept);
			return this;
		}

		private void addMedicationType() {
			medicationBuilder.withEventType(EventType.UNKNOWN);
		}

		private MedicationBuilderWrapper addDosageForm(MedicationStatement medicationStatement) {
			new MedicationDoseFormProcessor().execute(medicationStatement.getMedication(), medicationBuilder::withDoseForm);
			return this;
		}

		private MedicationBuilderWrapper addSubjectId(SubjectId subjectId) {
			medicationBuilder.withSubjectId(subjectId);
			return this;
		}

		private Medication build() {
			return medicationBuilder.build();
		}
	}
}
