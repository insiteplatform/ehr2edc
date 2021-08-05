package com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway;

import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class InProcessPatientGatewayFactory {

	private final InProcessPatientGateway inProcessPatientGateway;

	public InProcessPatientGatewayFactory(InProcessPatientGateway inProcessPatientGateway) {
		this.inProcessPatientGateway = inProcessPatientGateway;
	}

	public InProcessPatientGateway create(StudyId studyId) {
		return inProcessPatientGateway;
	}
}
