package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.EDCStudyGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContext;
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContextRepository;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;

@Component
class SubmittedEventHandler implements Consumer<SubmittedEvent> {
	private final StudyRepository studyRepository;
	private final EDCStudyGateway edcStudyGateway;
	private final SubmittedEventRepository submittedEventRepository;
	private final SubmissionContextRepository submissionContextRepository;

	public SubmittedEventHandler(StudyRepository studyRepository, EDCStudyGateway edcStudyGateway,
			SubmittedEventRepository submittedEventRepository,
			SubmissionContextRepository submissionContextRepository) {
		this.studyRepository = studyRepository;
		this.edcStudyGateway = edcStudyGateway;
		this.submittedEventRepository = submittedEventRepository;
		this.submissionContextRepository = submissionContextRepository;
	}

	@Override
	public void accept(SubmittedEvent submittedEvent) {
		Study study = studyRepository.getStudyById(submittedEvent.getStudyId());
		String submittedXml = edcStudyGateway.submitReviewedEvent(submittedEvent, study);
		submittedEventRepository.save(submittedEvent);
		createSubmissionContext(submittedEvent, submittedXml);
	}

	private void createSubmissionContext(SubmittedEvent submittedEvent, String submittedXml) {
		SubmissionContext submissionContext = SubmissionContext.newBuilder()
				.withSubmittedEventId(submittedEvent.getId())
				.withSubmittedXml(submittedXml)
				.build();
		submissionContextRepository.save(submissionContext);
	}
}
