package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange;
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange.DeregistrationRequest;
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange.RegistrationRequest;
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectRegistrationController {

	private RecordSubjectRegistrationChange recordSubjectRegistrationChange;

	public SubjectRegistrationController(RecordSubjectRegistrationChange recordSubjectRegistrationChange) {
		this.recordSubjectRegistrationChange = recordSubjectRegistrationChange;
	}

	void handleSubjectRegistered(SubjectRegisteredEvent event) {
		RegistrationRequest request = RegistrationRequest.newBuilder()
				.withPatientId(PatientCDWReference.newBuilder()
						.withId(event.getPatientId())
						.withSource(event.getNamespace())
						.build())
				.withStudyId(StudyId.of(event.getStudyId()))
				.withSubjectId(SubjectId.of(event.getSubjectId()))
				.withDate(event.getDate())
				.build();
		recordSubjectRegistrationChange.register(request);
	}

	public void handleSubjectDeregistered(SubjectDeregisteredEvent event) {
		DeregistrationRequest request = DeregistrationRequest.newBuilder()
				.withPatientId(event.getPatientCDWReference())
				.withStudyId(event.getStudyId())
				.withSubjectId(event.getSubjectId())
				.withDate(event.getDate())
				.withReason(event.getReason())
				.build();
		recordSubjectRegistrationChange.deregister(request);
	}
}
