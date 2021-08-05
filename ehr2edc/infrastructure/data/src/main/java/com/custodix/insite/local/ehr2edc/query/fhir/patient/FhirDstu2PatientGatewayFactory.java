package com.custodix.insite.local.ehr2edc.query.fhir.patient;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class FhirDstu2PatientGatewayFactory {

	private final FhirDstu2ClientFactory fhirDstu2ClientFactory;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;

	public FhirDstu2PatientGatewayFactory(FhirDstu2ClientFactory fhirDstu2ClientFactory,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.fhirDstu2ClientFactory = fhirDstu2ClientFactory;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	public FhirDstu2PatientGateway create(final StudyId studyId) {
		return new FhirDstu2PatientGateway(fhirDstu2ClientFactory.getFhirDstu2Client(studyId),
				fhirDstu2PatientRepository);
	}
}
