package com.custodix.insite.local.ehr2edc.query.fhir.laboratory;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2LaboratoryGateway implements EHRGateway<LabValues, LaboratoryQuery> {

	private final IGenericClient fhirDstu2Client;
	private final List<FhirDstu2LaboratoryQuery> fhirDstu2LaboratoryQueries;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2LaboratoryGateway(IGenericClient fhirDstu2Client,
			List<FhirDstu2LaboratoryQuery> fhirDstu2LaboratoryQueries,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.fhirDstu2Client = fhirDstu2Client;
		this.fhirDstu2LaboratoryQueries = fhirDstu2LaboratoryQueries;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof LaboratoryQuery;
	}

	@Override
	public LabValues execute(LaboratoryQuery laboratoryQuery, LocalDate referenceDate) {
		PatientCDWReference patientCDWReference = getPatientCDWReference(laboratoryQuery);
		Optional<FhirResourceId> fhirPatientResourceId = findFhirPatientResourceId(patientCDWReference);
		return fhirPatientResourceId.map(id -> getLabValues(laboratoryQuery, referenceDate, id))
				.orElse(new LabValues(emptyList()));
	}

	private LabValues getLabValues(LaboratoryQuery query, LocalDate referenceDate, FhirResourceId fhirPatientResourceId) {
		Stream<LabValue> labValues = fhirDstu2LaboratoryQueries.stream()
				.flatMap(fhirDstu2LaboratoryQuery -> process(query, referenceDate, fhirDstu2LaboratoryQuery,
						fhirPatientResourceId));
		return new LabValues(labValues.collect(Collectors.toList()));
	}

	private Stream<LabValue> process(LaboratoryQuery query, LocalDate referenceDate,
			FhirDstu2LaboratoryQuery fhirDstu2LaboratoryQuery, FhirResourceId fhirPatientResourceId) {
		return fhirDstu2LaboratoryQuery.query(fhirDstu2Client, query, referenceDate, fhirPatientResourceId);
	}

	private Optional<FhirResourceId> findFhirPatientResourceId(PatientCDWReference patientCDWReference) {
		return fhirDstu2PatientRepository.findFhirResourceId(fhirDstu2Client, patientCDWReference);
	}

	private PatientCDWReference getPatientCDWReference(LaboratoryQuery laboratoryQuery) {
		return laboratoryQuery.getCriteria()
				.subject()
				.getPatientCDWReference();
	}
}
