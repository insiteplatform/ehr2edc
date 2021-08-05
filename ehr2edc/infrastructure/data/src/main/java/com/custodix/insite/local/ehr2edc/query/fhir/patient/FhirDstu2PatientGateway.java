package com.custodix.insite.local.ehr2edc.query.fhir.patient;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.patient.PatientDomain;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.patient.PatientEHRGatewayStrategy;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.NamingSystem;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FhirDstu2PatientGateway implements PatientEHRGatewayStrategy {

	private final IGenericClient client;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2PatientGateway(final IGenericClient client, final FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.client = client;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean exists(PatientSearchCriteria patientSearchCriteria) {
		return fhirDstu2PatientRepository
				.findPatient(client, patientSearchCriteria)
				.findFirst()
				.isPresent();
	}

	@Override
	public Set<PatientCDWReference> getFiltered(String patientDomain, String filter, int limit) {
		final Stream<Patient> patients = fhirDstu2PatientRepository.findPatients(client, patientDomain, filter, limit);
		return patients
				.map(patient -> patient.getIdentifier().stream()
						.filter(id -> id.getSystem().contains(patientDomain)).findFirst())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(FhirDstu2PatientGateway::getPatientCDWReference)
				.collect(Collectors.toSet());
	}

	@Override
	public List<PatientDomain> getPatientDomains() {
		final FhirResourceRepository<NamingSystem> repo = new FhirResourceRepository<>(client, NamingSystem.class);
		final FhirQuery query = FhirQuery.newBuilder()
				.withCriteria(Collections.emptyList())
				.build();
		return repo.find(query)
				.flatMap(ns -> ns.getUniqueId().stream())
				.filter(id -> Objects.equals(id.getType(), "uri"))
				.map(NamingSystem.UniqueId::getValue)
				.map(PatientDomain::of)
				.collect(Collectors.toList());
	}

	private static PatientCDWReference getPatientCDWReference(IdentifierDt id) {
		return PatientCDWReference.newBuilder()
				.withSource(id.getSystem())
				.withId(id.getValue())
				.build();
	}
}
