package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2GatewayFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2MedicationGatewayFactory implements FhirDstu2GatewayFactory<Medications, MedicationQuery> {

	private final List<FhirDstu2MedicationQuery> medicationQueries;
	private final FhirDstu2ClientFactory fhirDstu2ClientFactory;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2MedicationGatewayFactory(List<FhirDstu2MedicationQuery> medicationQueries,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.medicationQueries = medicationQueries;
		this.fhirDstu2ClientFactory = fhirDstu2ClientFactory;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof MedicationQuery;
	}

	@Override
	public EHRGateway<Medications, MedicationQuery> create(MedicationQuery query,
			PopulationSpecification specification) {
		IGenericClient fhirDstu2Client = fhirDstu2ClientFactory.getFhirDstu2Client(specification.getStudyId());
		return new FhirDstu2MedicationGateway(fhirDstu2Client, medicationQueries,
				fhirDstu2PatientRepository);
	}
}
