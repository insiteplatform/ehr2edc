package com.custodix.insite.mongodb.export.patient.application.command;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject;
import com.custodix.insite.mongodb.export.patient.domain.repository.ActiveSubjectEHRGateway;

@Validated
public class DeactivateSubjectCommand implements DeactivateSubject {

	private final ActiveSubjectEHRGateway activeSubjectEHRGateway;

	public DeactivateSubjectCommand(final ActiveSubjectEHRGateway activeSubjectEHRGateway) {
		this.activeSubjectEHRGateway = activeSubjectEHRGateway;
	}

	@Override
	public void deactivate(@Valid final Request request) {
		activeSubjectEHRGateway.delete(request.getSubjectId());
	}
}
