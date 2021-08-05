package com.custodix.insite.local.ehr2edc.query.fhir.laboratory;

import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2GatewayFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2LaboratoryGatewayFactory implements FhirDstu2GatewayFactory<LabValues, LaboratoryQuery> {

	private final List<FhirDstu2LaboratoryQuery> laboratoryQueries;
	private final FhirDstu2ClientFactory fhirDstu2ClientFactory;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2LaboratoryGatewayFactory(List<FhirDstu2LaboratoryQuery> laboratoryQueries,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.laboratoryQueries = laboratoryQueries;
		this.fhirDstu2ClientFactory = fhirDstu2ClientFactory;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof LaboratoryQuery;
	}

	@Override
	public EHRGateway<LabValues, LaboratoryQuery> create(LaboratoryQuery query, PopulationSpecification specification) {
		IGenericClient fhirDstu2Client = fhirDstu2ClientFactory.getFhirDstu2Client(specification.getStudyId());
		return new FhirDstu2LaboratoryGateway(fhirDstu2Client, laboratoryQueries, fhirDstu2PatientRepository);
	}
}
