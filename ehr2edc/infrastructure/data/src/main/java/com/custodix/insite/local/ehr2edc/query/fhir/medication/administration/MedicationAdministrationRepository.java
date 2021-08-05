package com.custodix.insite.local.ehr2edc.query.fhir.medication.administration;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory.ResourceField;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory.ResourceFields;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class MedicationAdministrationRepository {
	private final FhirQueryFactory fhirQueryFactory;

	MedicationAdministrationRepository(FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	Stream<MedicationAdministration> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		return getMedicationAdministrations(client, medicationQuery, referenceDate, patientResourceId);
	}

	private Stream<MedicationAdministration> getMedicationAdministrations(IGenericClient client,
			MedicationQuery medicationQuery, LocalDate referenceDate, FhirResourceId patientResourceId) {
		final List<Include> includes = singletonList(MedicationAdministration.INCLUDE_MEDICATION);
		final FhirQuery fhirQuery = fhirQueryFactory.create(medicationQuery, patientResourceId, referenceDate,
				createResourceFields(), includes);
		return new FhirResourceRepository<>(client, MedicationAdministration.class).find(fhirQuery);
	}

	private ResourceFields createResourceFields() {
		return ResourceFields.newBuilder()
				.withPatientField(ResourceField.of(MedicationAdministration.SP_PATIENT))
				.withDateField(ResourceField.of(MedicationAdministration.SP_EFFECTIVETIME))
				.withCodeField(ResourceField.of(MedicationAdministration.SP_CODE))
				.build();
	}
}
