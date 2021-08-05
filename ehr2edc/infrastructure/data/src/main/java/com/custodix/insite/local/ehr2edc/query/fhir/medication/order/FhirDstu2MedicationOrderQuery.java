package com.custodix.insite.local.ehr2edc.query.fhir.medication.order;

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

public class FhirDstu2MedicationOrderQuery implements FhirDstu2MedicationQuery {

	private final MedicationOrderRepository repo;
	private final MedicationFactoryForMedicationOrder medicationFactory;

	public FhirDstu2MedicationOrderQuery(final MedicationOrderRepository repo,
			final MedicationFactoryForMedicationOrder medicationFactory) {
		this.repo = repo;
		this.medicationFactory = medicationFactory;
	}

	@Override
	public Stream<Medication> query(final IGenericClient client,
			final MedicationQuery query,
			final LocalDate referenceDate,
			final FhirResourceId fhirPatientId) {
		final SubjectId subjectId = query.getCriteria().subject().getSubjectId();
		final Stream<Medication> medication = repo.query(client, query, referenceDate, fhirPatientId)
				.flatMap(order -> medicationFactory.create(order, subjectId));
		final Predicate<Medication> freshness = query.getCriteria().freshness()
				.map(criterion -> criterion.getInterval(referenceDate))
				.map(interval -> (Predicate<Medication>) new MedicationFreshnessMatcher(interval))
				.orElse(m -> true);
		return medication.filter(freshness);
	}
}
