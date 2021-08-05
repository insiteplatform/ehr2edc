package com.custodix.insite.local.ehr2edc.query.fhir.medication.administration;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.FhirDstu2MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2MedicationAdministrationQuery implements FhirDstu2MedicationQuery {

	private final MedicationAdministrationRepository medicationAdministrationRepository;
	private final MedicationFactoryForMedicationAdministration medicationFactoryForMedicationAdministration;

	FhirDstu2MedicationAdministrationQuery(
			MedicationAdministrationRepository medicationAdministrationRepository,
			MedicationFactoryForMedicationAdministration medicationFactoryForMedicationAdministration) {
		this.medicationAdministrationRepository = medicationAdministrationRepository;
		this.medicationFactoryForMedicationAdministration = medicationFactoryForMedicationAdministration;
	}

	@Override
	public Stream<Medication> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId fhirPatientResourceId) {
		final Stream<MedicationAdministration> medicationAdministrations = medicationAdministrationRepository
				.query(client, medicationQuery, referenceDate, fhirPatientResourceId);
		final SubjectId subjectId = medicationQuery.getCriteria().subject().getSubjectId();
		return medicationAdministrations.map(medicationAdministration ->
				medicationFactoryForMedicationAdministration.create(medicationAdministration, subjectId));
	}
}
