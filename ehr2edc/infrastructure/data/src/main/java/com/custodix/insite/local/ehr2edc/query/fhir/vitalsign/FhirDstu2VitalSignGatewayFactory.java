package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign;

import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2GatewayFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class FhirDstu2VitalSignGatewayFactory implements FhirDstu2GatewayFactory<VitalSigns, VitalSignQuery> {

	private final List<FhirDstu2VitalSignQuery> vitalSignQueries;
	private final FhirDstu2ClientFactory fhirDstu2ClientFactory;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	FhirDstu2VitalSignGatewayFactory(List<FhirDstu2VitalSignQuery> vitalSignQueries,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.vitalSignQueries = vitalSignQueries;
		this.fhirDstu2ClientFactory = fhirDstu2ClientFactory;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof VitalSignQuery;
	}

	@Override
	public EHRGateway<VitalSigns, VitalSignQuery> create(VitalSignQuery query, PopulationSpecification specification) {
		IGenericClient fhirDstu2Client = fhirDstu2ClientFactory.getFhirDstu2Client(specification.getStudyId());
		return new FhirDstu2VitalSignGateway(fhirDstu2Client, vitalSignQueries,
				fhirDstu2PatientRepository);
	}
}
