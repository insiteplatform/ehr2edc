package com.custodix.insite.local.ehr2edc.query.fhir.medication.order;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class MedicationOrderRepository {

	private static final FhirQueryFactory.ResourceFields FIELDS = FhirQueryFactory.ResourceFields.newBuilder()
			.withPatientField(FhirQueryFactory.ResourceField.of(MedicationOrder.SP_PATIENT))
			.withDateField(FhirQueryFactory.ResourceField.of(MedicationOrder.SP_DATEWRITTEN))
			.build();
	private static final List<Include> INCLUDES = singletonList(MedicationOrder.INCLUDE_MEDICATION);

	private final FhirQueryFactory fhirQueryFactory;

	public MedicationOrderRepository(final FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	public Stream<MedicationOrder> query(final IGenericClient client,
			final MedicationQuery query,
			final LocalDate referenceDate,
			final FhirResourceId fhirPatientId) {
		final FhirQuery fhirQuery = fhirQueryFactory.create(query, fhirPatientId, referenceDate, FIELDS, INCLUDES);
		return new FhirResourceRepository<>(client, MedicationOrder.class)
				.find(fhirQuery);
	}
}
