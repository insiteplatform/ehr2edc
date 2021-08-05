package com.custodix.insite.local.ehr2edc.query.patient;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientGatewayFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGatewayFactory;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class PatientEHRGatewayFactory {

	private final EHRConnectionRepository ehrConnectionRepository;
	private final FhirDstu2PatientGatewayFactory fhirDstu2PatientGatewayFactory;
	private final InProcessPatientGatewayFactory inProcessPatientGatewayFactory;

	public PatientEHRGatewayFactory(EHRConnectionRepository ehrConnectionRepository,
			FhirDstu2PatientGatewayFactory fhirDstu2PatientGatewayFactory,
			InProcessPatientGatewayFactory inProcessPatientGatewayFactory) {
		this.ehrConnectionRepository = ehrConnectionRepository;
		this.fhirDstu2PatientGatewayFactory = fhirDstu2PatientGatewayFactory;
		this.inProcessPatientGatewayFactory = inProcessPatientGatewayFactory;
	}

	PatientEHRGatewayStrategy selectGateway(StudyId studyId) {
		Optional<EHRConnection> ehrConnection = ehrConnectionRepository.findByStudyId(studyId);

		return isFhir(ehrConnection) ?
				fhirDstu2PatientGatewayFactory.create(studyId) :
				inProcessPatientGatewayFactory.create(studyId);
	}

	public boolean isFhir(StudyId studyId) {
		return isFhir(ehrConnectionRepository.findByStudyId(studyId));
	}

	private boolean isFhir(Optional<EHRConnection> ehrConnection) {
		return ehrConnection.isPresent() && EHRSystem.FHIR.equals(ehrConnection.get()
				.getSystem());
	}

}
