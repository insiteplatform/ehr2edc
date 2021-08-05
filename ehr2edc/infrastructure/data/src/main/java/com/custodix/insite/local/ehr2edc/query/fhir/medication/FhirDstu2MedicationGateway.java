package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2MedicationGateway implements EHRGateway<Medications, MedicationQuery>  {

	private final IGenericClient fhirDstu2Client;
	private final List<FhirDstu2MedicationQuery> fhirDstu2MedicationQueries;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2MedicationGateway(IGenericClient fhirDstu2Client,
			List<FhirDstu2MedicationQuery> fhirDstu2MedicationQueries,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.fhirDstu2Client = fhirDstu2Client;
		this.fhirDstu2MedicationQueries = fhirDstu2MedicationQueries;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof MedicationQuery;
	}

	@Override
	public Medications execute(MedicationQuery medicationQuery, LocalDate referenceDate) {
		Optional<FhirResourceId> fhirPatientResourceId = findFhirPatientResourceId(medicationQuery.getCriteria()
				.subject()
				.getPatientCDWReference());
		return fhirPatientResourceId.map(id -> getMedications(medicationQuery, referenceDate, id))
				.orElse(new Medications(emptyList()));
	}

	private Medications getMedications(MedicationQuery query, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId) {
		List<Medication> medications = fhirDstu2MedicationQueries.stream()
				.flatMap(fhirDstu2MedicationQuery ->
						process(query, referenceDate, fhirDstu2MedicationQuery,	fhirPatientResourceId))
				.collect(Collectors.toList());
		return new Medications(medications);
	}

	private Stream<Medication> process(MedicationQuery query, LocalDate referenceDate,
			FhirDstu2MedicationQuery fhirDstu2MedicationQuery, FhirResourceId fhirPatientResourceId) {
		return fhirDstu2MedicationQuery.query(fhirDstu2Client, query, referenceDate, fhirPatientResourceId);
	}

	private Optional<FhirResourceId> findFhirPatientResourceId(PatientCDWReference patientCDWReference) {
		return fhirDstu2PatientRepository.findFhirResourceId(fhirDstu2Client, patientCDWReference);
	}
}
