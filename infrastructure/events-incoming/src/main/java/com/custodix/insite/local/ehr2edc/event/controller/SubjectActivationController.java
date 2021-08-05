package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class SubjectActivationController {

	private final ActivateSubject activateSubject;

	public SubjectActivationController(final ActivateSubject activateSubject) {
		this.activateSubject = activateSubject;
	}

	void handleSubjectRegistered(SubjectRegisteredEvent event) {
		activateSubject.activate(createRequest(event));
	}

	private ActivateSubject.Request createRequest(final SubjectRegisteredEvent event) {
		return ActivateSubject.Request.newBuilder()
		.withPatientIdentifier(toPatientIdentifier(event))
		.build();
	}

	private PatientIdentifier toPatientIdentifier(SubjectRegisteredEvent event) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(event.getPatientId()))
				.withNamespace(Namespace.of(event.getNamespace()))
				.withSubjectId(SubjectId.of(event.getSubjectId()))
				.build();
	}
}
