package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

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
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class MedicationStatementRepository {
	private final FhirQueryFactory fhirQueryFactory;

	MedicationStatementRepository(FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	Stream<MedicationStatement> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		return getMedicationStatements(client, medicationQuery, referenceDate, patientResourceId);
	}

	private Stream<MedicationStatement> getMedicationStatements(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		final ResourceFields fields = createResourceFields();
		final List<Include> includes = singletonList(MedicationStatement.INCLUDE_MEDICATION);
		final FhirQuery fhirQuery = fhirQueryFactory
				.create(medicationQuery, patientResourceId, referenceDate, fields, includes);
		return new FhirResourceRepository<>(client, MedicationStatement.class).find(fhirQuery);
	}

	private ResourceFields createResourceFields() {
		return ResourceFields.newBuilder()
				.withPatientField(ResourceField.of(MedicationStatement.SP_PATIENT))
				.withDateField(ResourceField.of(MedicationStatement.SP_EFFECTIVEDATE))
				.withCodeField(ResourceField.of(MedicationStatement.SP_CODE))
				.build();
	}
}
