package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.FhirDstu2MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2MedicationStatementQuery implements FhirDstu2MedicationQuery {

	private final MedicationStatementRepository medicationStatementRepository;
	private final MedicationFactoryForMedicationStatement	medicationFactoryForMedicationStatement;

	FhirDstu2MedicationStatementQuery(MedicationStatementRepository medicationStatementRepository,
			MedicationFactoryForMedicationStatement medicationFactoryForMedicationStatement) {
		this.medicationStatementRepository = medicationStatementRepository;
		this.medicationFactoryForMedicationStatement = medicationFactoryForMedicationStatement;
	}

	@Override
	public Stream<Medication> query(IGenericClient client, MedicationQuery medicationQuery,
			LocalDate referenceDate, FhirResourceId fhirPatientResourceId) {
		final Stream<MedicationStatement> medicationResources = medicationStatementRepository
				.query(client, medicationQuery, referenceDate, fhirPatientResourceId);
		final SubjectId subjectId = medicationQuery.getCriteria().subject().getSubjectId();
		return medicationResources.flatMap(medicationResource ->
						medicationFactoryForMedicationStatement.create(medicationResource, subjectId));
	}
}
