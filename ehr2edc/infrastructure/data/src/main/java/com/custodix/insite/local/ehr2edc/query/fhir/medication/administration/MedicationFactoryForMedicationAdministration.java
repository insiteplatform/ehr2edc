package com.custodix.insite.local.ehr2edc.query.fhir.medication.administration;

import java.time.ZoneId;
import java.util.Date;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.DosageUnitProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.DosageValueProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.MedicationConceptCodeProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.MedicationDoseFormProcessor;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;

class MedicationFactoryForMedicationAdministration {

	Medication create(MedicationAdministration medicationAdministration, SubjectId subjectId) {
		Medication.Builder medicationBuilder = Medication.newBuilder();
		medicationBuilder.withEventType(EventType.ADMINISTRATION);
		addConcept(medicationAdministration, medicationBuilder);
		addDoseForm(medicationAdministration, medicationBuilder);
		addSubjectId(subjectId, medicationBuilder);
		addStartAndEndDate(medicationAdministration, medicationBuilder);
		addRoute(medicationAdministration, medicationBuilder);
		addDosage(medicationAdministration, medicationBuilder);
		return medicationBuilder.build();
	}

	private void addRoute(MedicationAdministration medicationResource, Medication.Builder medicationBuilder) {
		if(hasNoDosageRoute(medicationResource)){
			return;
		}
		medicationBuilder.withAdministrationRoute(medicationResource.getDosage().getRoute().getCoding().get(0).getCode());
	}

	private boolean hasNoDosageRoute(MedicationAdministration medicationResource) {
		return medicationResource.getDosage() == null ||
				medicationResource.getDosage().getRoute() == null ||
				medicationResource.getDosage().getRoute().getCoding() == null ||
				medicationResource.getDosage().getRoute().getCoding().isEmpty();
	}

	private void addDosage(MedicationAdministration medicationResource, Medication.Builder medicationBuilder) {
		if (hasNoDosage(medicationResource)) {
			return;
		}
		Dosage.Builder dosageBuilder = Dosage.newBuilder();
		SimpleQuantityDt dosage = medicationResource.getDosage()
				.getQuantity();
		new DosageValueProcessor().execute(dosage, dosageBuilder::withValue);
		new DosageUnitProcessor().execute(dosage, dosageBuilder::withUnit);
		medicationBuilder.withDosage(dosageBuilder.build());
	}

	private boolean hasNoDosage(MedicationAdministration medicationResource) {
		return medicationResource.getDosage() == null ||
				medicationResource.getDosage().getQuantity() == null ||
				medicationResource.getDosage().getQuantity().getValue() == null;
	}

	private void addStartAndEndDate(MedicationAdministration medicationAdministration, Medication.Builder medicationBuilder) {
		if(medicationAdministration.getEffectiveTime() == null){
			return;
		}
		PeriodDt effectiveTime = (PeriodDt) medicationAdministration.getEffectiveTime();
		addStartDate(effectiveTime.getStart(), medicationBuilder);
		addEndDate(effectiveTime.getEnd(), medicationBuilder);
	}

	private void addStartDate(Date startDate, Medication.Builder medicationBuilder) {
		if(startDate == null) {
			return;
		}
		medicationBuilder.withStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	private void addEndDate(Date endDate, Medication.Builder medicationBuilder) {
		if(endDate == null) {
			return;
		}
		medicationBuilder.withEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	private void addSubjectId(SubjectId subjectId, Medication.Builder medicationBuilder) {
		medicationBuilder.withSubjectId(subjectId);
	}

	private void addDoseForm(MedicationAdministration medicationAdministration, Medication.Builder medicationBuilder) {
		new MedicationDoseFormProcessor().execute(medicationAdministration.getMedication(), medicationBuilder::withDoseForm);
	}

	private void addConcept(MedicationAdministration medicationAdministration,
			Medication.Builder medicationBuilder) {
		new MedicationConceptCodeProcessor().execute(
				medicationAdministration.getMedication(), medicationBuilder::withConcept);
	}
}
