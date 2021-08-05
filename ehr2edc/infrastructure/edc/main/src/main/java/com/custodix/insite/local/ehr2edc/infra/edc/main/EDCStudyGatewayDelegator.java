package com.custodix.insite.local.ehr2edc.infra.edc.main;

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.READ_SUBJECTS;
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.SUBMIT_EVENT;
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.WRITE_SUBJECT;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.EDCStudyGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

class EDCStudyGatewayDelegator implements EDCStudyGateway {
	private final StudyConnectionRepository studyConnectionRepository;
	private final SpecificEDCStudyGatewayProvider specificEdcStudyGatewayProvider;

	EDCStudyGatewayDelegator(StudyConnectionRepository studyConnectionRepository,
			SpecificEDCStudyGatewayProvider specificEdcStudyGatewayProvider) {
		this.studyConnectionRepository = studyConnectionRepository;
		this.specificEdcStudyGatewayProvider = specificEdcStudyGatewayProvider;
	}

	@Override
	public RegisteredSubjects findRegisteredSubjectIds(StudyId studyId) {
		Optional<ExternalEDCConnection> connection = studyConnectionRepository.findActive(studyId, READ_SUBJECTS);
		return connection.map(c -> RegisteredSubjects.fromEdc(specificEdcStudyGatewayProvider.get(c)
				.findRegisteredSubjectIds(c)))
				.orElse(RegisteredSubjects.noEdc());
	}

	@Override
	public Optional<Boolean> isRegisteredSubject(StudyId studyId, EDCSubjectReference reference) {
		Optional<ExternalEDCConnection> connection = studyConnectionRepository.findActive(studyId, READ_SUBJECTS);
		return connection.map(c -> specificEdcStudyGatewayProvider.get(c)
				.isRegisteredSubject(c, reference));
	}

	@Override
	public void createSubject(Study study, EDCSubjectReference reference) {
		Optional<ExternalEDCConnection> connection = studyConnectionRepository.findActive(study.getStudyId(),
				WRITE_SUBJECT);
		connection.ifPresent(c -> specificEdcStudyGatewayProvider.get(c)
				.createSubject(c, study, reference));
	}

	@Override
	public String submitReviewedEvent(SubmittedEvent reviewedEvent, Study study) {
		ExternalEDCConnection connection = studyConnectionRepository.getActive(study.getStudyId(), SUBMIT_EVENT);
		return specificEdcStudyGatewayProvider.get(connection)
				.submitReviewedEvent(connection, reviewedEvent, study);
	}
}
