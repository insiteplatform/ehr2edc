package com.custodix.insite.local.ehr2edc.query.patient;

import java.util.List;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.patient.PatientDomain;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public class PatientEHRGatewayImpl implements PatientEHRGateway {

	private final PatientEHRGatewayFactory patientEHRGatewayFactory;

	PatientEHRGatewayImpl(PatientEHRGatewayFactory patientEHRGatewayFactory) {
		this.patientEHRGatewayFactory = patientEHRGatewayFactory;
	}

	@Override
	public boolean exists(StudyId studyId, PatientSearchCriteria patientSearchCriteria) {
		final PatientEHRGatewayStrategy gateway = patientEHRGatewayFactory.selectGateway(studyId);
		return gateway.exists(patientSearchCriteria);
	}

	@Override
	public Set<PatientCDWReference> getFiltered(StudyId studyId, String patientDomain, String filter, int limit) {
		final PatientEHRGatewayStrategy gateway = patientEHRGatewayFactory.selectGateway(studyId);
		return gateway.getFiltered(patientDomain, filter, limit);
	}

	@Override
	public List<PatientDomain> getPatientDomains(StudyId studyId) {
		final PatientEHRGatewayStrategy gateway = patientEHRGatewayFactory.selectGateway(studyId);
		return gateway.getPatientDomains();
	}
}
