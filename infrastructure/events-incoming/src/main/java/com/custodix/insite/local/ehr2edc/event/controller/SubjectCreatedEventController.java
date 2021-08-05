package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.command.CreateSubjectInEDC;
import com.custodix.insite.local.ehr2edc.events.SubjectCreated;

public class SubjectCreatedEventController implements EHR2EDCAsyncEventController<SubjectCreated> {
	private final CreateSubjectInEDC createSubjectInEDC;

	public SubjectCreatedEventController(CreateSubjectInEDC createSubjectInEDC) {
		this.createSubjectInEDC = createSubjectInEDC;
	}

	public void handle(SubjectCreated event) {
		createSubjectInEDC.create(toRequest(event));
	}

	private CreateSubjectInEDC.Request toRequest(SubjectCreated event) {
		return CreateSubjectInEDC.Request.newBuilder()
				.withStudyId(event.getStudyId())
				.withEdcSubjectReference(event.getEdcSubjectReference())
				.build();
	}
}
