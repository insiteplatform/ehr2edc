package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent;
import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class SubjectDeactivationController {

	private final DeactivateSubject deactivateSubject;

	public SubjectDeactivationController(final DeactivateSubject deactivateSubject) {
		this.deactivateSubject = deactivateSubject;
	}

	public void handleSubjectDeregistered(SubjectDeregisteredEvent event) {
		deactivateSubject.deactivate(createRequest(event));
	}

	private DeactivateSubject.Request createRequest(final SubjectDeregisteredEvent event) {
		return DeactivateSubject.Request.newBuilder()
				.withSubjectId(toSubjectId(event))
				.build();
	}

	private SubjectId toSubjectId(SubjectDeregisteredEvent event) {
		return SubjectId.of(event.getSubjectId().getId());
	}
}
