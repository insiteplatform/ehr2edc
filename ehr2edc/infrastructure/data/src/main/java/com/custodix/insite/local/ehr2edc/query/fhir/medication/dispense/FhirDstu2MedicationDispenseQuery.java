package com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.FhirDstu2MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.MedicationFreshnessMatcher;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2MedicationDispenseQuery implements FhirDstu2MedicationQuery {

	private final MedicationDispenseRepository medicationDispenseRepository;
	private final MedicationFactoryForMedicationDispense medicationFactoryForMedicationDispense;

	FhirDstu2MedicationDispenseQuery(
			MedicationDispenseRepository medicationDispenseRepository,
			MedicationFactoryForMedicationDispense medicationFactoryForMedicationDispense) {
		this.medicationDispenseRepository = medicationDispenseRepository;
		this.medicationFactoryForMedicationDispense = medicationFactoryForMedicationDispense;
	}

	@Override
	public Stream<Medication> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId fhirPatientResourceId) {
		final SubjectId subjectId = medicationQuery.getCriteria().subject().getSubjectId();
		final Stream<Medication> medication = medicationDispenseRepository
				.query(client, medicationQuery, referenceDate, fhirPatientResourceId)
				.flatMap(medicationDispense -> medicationFactoryForMedicationDispense
						.create(medicationDispense, subjectId));
		final Predicate<Medication> freshness = medicationQuery.getCriteria().freshness()
				.map(criterion -> criterion.getInterval(referenceDate))
				.map(interval -> (Predicate<Medication>) new MedicationFreshnessMatcher(interval))
				.orElse(m -> true);
		return medication.filter(freshness);
	}
}
