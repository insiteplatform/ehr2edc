package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.util.List;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class OpenClinicaEDCStudyGateway implements SpecificEDCStudyGateway {
	private final OpenClinicaGetSubjects getSubjects;
	private final OpenClinicaCreateSubject createSubject;
	private final OpenClinicaIsRegisteredSubject isRegisteredSubject;
	private final OpenClinicaSubmitEvent submitEvent;

	OpenClinicaEDCStudyGateway(OpenClinicaGetSubjects getSubjects, OpenClinicaCreateSubject createSubject,
			OpenClinicaIsRegisteredSubject isRegisteredSubject, OpenClinicaSubmitEvent submitEvent) {
		this.getSubjects = getSubjects;
		this.createSubject = createSubject;
		this.isRegisteredSubject = isRegisteredSubject;
		this.submitEvent = submitEvent;
	}

	@Override
	public boolean supports(EDCSystem edcSystem) {
		return edcSystem == EDCSystem.OPEN_CLINICA;
	}

	@Override
	public List<EDCSubjectReference> findRegisteredSubjectIds(ExternalEDCConnection connection) {
		return getSubjects.getSubjects(connection);
	}

	@Override
	public boolean isRegisteredSubject(ExternalEDCConnection connection, EDCSubjectReference reference) {
		return isRegisteredSubject.isRegisteredSubject(connection, reference);
	}

	@Override
	public void createSubject(ExternalEDCConnection connection, Study study, EDCSubjectReference reference) {
		createSubject.create(connection, reference);
	}

	@Override
	public String submitReviewedEvent(ExternalEDCConnection connection, SubmittedEvent reviewedEvent, Study study) {
		return submitEvent.submitEvent(connection, reviewedEvent, study);
	}
}
