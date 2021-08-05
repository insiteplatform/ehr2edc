package com.custodix.insite.local.ehr2edc.ehr.epic.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.epic.command.PatientRegistration;
import com.custodix.insite.local.ehr2edc.ehr.epic.domain.event.EpicPortalPatientRegistered;
import com.custodix.insite.local.ehr2edc.ehr.epic.vocabulary.OAuthAccessToken;

import eu.ehr4cr.workbench.local.eventhandlers.EventHandler;

public class PatientRegistrationController implements EventHandler {

	private PatientRegistration patientRegistration;

	public PatientRegistrationController(PatientRegistration patientRegistration) {
		this.patientRegistration = patientRegistration;
	}

	@Override
	public void handle(Object event) {
		if(isEpicPortalPatientRegisteredEvent(event)) {
			patientRegistration.register(createRequest((EpicPortalPatientRegistered)event));
		}
	}

	private PatientRegistration.Request createRequest(EpicPortalPatientRegistered event) {
		return PatientRegistration.Request.newBuilder()
				.withLastName(event.getLastName())
				.withFirstName(event.getFirstName())
				.withBirthDate(event.getBirthDate())
				.withOAuthAccessToken(OAuthAccessToken.of(event.getOAuthAccessToken()))
				.build();
	}

	private boolean isEpicPortalPatientRegisteredEvent(Object event) {
		return event instanceof EpicPortalPatientRegistered;
	}
}
