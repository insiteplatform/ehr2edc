package com.custodix.insite.mongodb.export.patient.application.command;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject;
import com.custodix.insite.mongodb.export.patient.domain.model.ActiveSubject;
import com.custodix.insite.mongodb.export.patient.domain.repository.ActiveSubjectEHRGateway;
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Validated
public class ActivateSubjectCommand implements ActivateSubject {

	private final ActiveSubjectEHRGateway activeSubjectEHRGateway;
	private final PatientNamespaceRepository patientNamespaceRepository;

	public ActivateSubjectCommand(final ActiveSubjectEHRGateway activeSubjectEHRGateway,
			PatientNamespaceRepository patientNamespaceRepository) {
		this.activeSubjectEHRGateway = activeSubjectEHRGateway;
		this.patientNamespaceRepository = patientNamespaceRepository;
	}

	@Override
	public void activate(@Valid final Request request) {
		if(isValidRequest(request)) {
			activeSubjectEHRGateway.save(ActiveSubject.instanceFor(request.getPatientIdentifier()));
		}
	}

	private boolean isValidRequest(Request request) {
		return canHandlePatient(request.getPatientIdentifier()) && hasNoActiveSubject(request.getPatientIdentifier().getSubjectId());
	}

	private boolean canHandlePatient(PatientIdentifier patientIdentifier) {
		return patientNamespaceRepository.exists(patientIdentifier.getNamespace());
	}

	private boolean hasNoActiveSubject(SubjectId subjectId) {
		return !activeSubjectEHRGateway.findBySubjectId(subjectId).isPresent();
	}
}
