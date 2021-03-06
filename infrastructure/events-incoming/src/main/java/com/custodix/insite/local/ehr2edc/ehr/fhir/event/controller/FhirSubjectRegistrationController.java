package com.custodix.insite.local.ehr2edc.ehr.fhir.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.fhir.command.FhirSubjectRegistration;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import eu.ehr4cr.workbench.local.eventhandlers.EventHandler;

public class FhirSubjectRegistrationController implements EventHandler {

	private final FhirSubjectRegistration fhirSubjectRegistration;

	public FhirSubjectRegistrationController(final FhirSubjectRegistration fhirSubjectRegistration) {
		this.fhirSubjectRegistration = fhirSubjectRegistration;
	}

	@Override
	public void handle(Object event) {
		if (event instanceof SubjectRegisteredEvent) {
			final FhirSubjectRegistration.Request request = getRequest((SubjectRegisteredEvent) event);
			fhirSubjectRegistration.register(request);
		}
	}

	private FhirSubjectRegistration.Request getRequest(final SubjectRegisteredEvent subjectRegisteredEvent) {
		PatientCDWReference patientCDWReference = PatientCDWReference.newBuilder()
				.withId(subjectRegisteredEvent.getPatientId())
				.withSource(subjectRegisteredEvent.getNamespace())
				.build();
		StudyId studyId = StudyId.of(subjectRegisteredEvent.getStudyId());
		SubjectId subjectId = SubjectId.of(subjectRegisteredEvent.getSubjectId());
		return FhirSubjectRegistration.Request.newBuilder()
				.withPatientId(patientCDWReference)
				.withStudyId(studyId)
				.withSubjectId(subjectId)
				.build();
	}
}
