package com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense;

import static ca.uhn.fhir.model.dstu2.resource.MedicationDispense.*;
import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory.ResourceField;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory.ResourceFields;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class MedicationDispenseRepository {
	private final FhirQueryFactory fhirQueryFactory;

	MedicationDispenseRepository(FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	Stream<MedicationDispense> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		return getMedicationDispenses(client, medicationQuery, referenceDate,
				patientResourceId);
	}

	private Stream<MedicationDispense> getMedicationDispenses(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {

		final FhirResourceRepository<MedicationDispense> repo =
				new FhirResourceRepository<>(client, MedicationDispense.class);
		final List<Include> includes = singletonList(INCLUDE_MEDICATION);

		final ResourceFields whenHandedOver = createResourceFields(ResourceField.of(SP_WHENHANDEDOVER));
		final FhirQuery whenHandedOverQuery = fhirQueryFactory
				.create(medicationQuery, patientResourceId, referenceDate, whenHandedOver, includes);
		final Stream<MedicationDispense> medicationDispenseWithHandOverTime = repo.find(whenHandedOverQuery);

		final ResourceFields whenPrepared = createResourceFields(ResourceField.of(SP_WHENPREPARED));
		final FhirQuery whenPreparedQuery = fhirQueryFactory
				.create(medicationQuery, patientResourceId, referenceDate, whenPrepared, includes);
		final Stream<MedicationDispense> medicationDispenseWithPreparedTime = repo.find(whenPreparedQuery);

		return  Stream.concat(medicationDispenseWithHandOverTime, medicationDispenseWithPreparedTime)
				.filter(distinctByKey(MedicationDispense::getId));
	}

	private ResourceFields createResourceFields(ResourceField dateField) {
		return ResourceFields.newBuilder()
				.withPatientField(ResourceField.of(SP_PATIENT))
				.withDateField(dateField)
				.withCodeField(ResourceField.of(SP_CODE))
				.build();
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}
}
