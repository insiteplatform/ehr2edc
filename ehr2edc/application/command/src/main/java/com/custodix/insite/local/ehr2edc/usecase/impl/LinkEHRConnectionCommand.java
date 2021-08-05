package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.LinkEHRConnection;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Command
class LinkEHRConnectionCommand implements LinkEHRConnection {
	private final StudyRepository studyRepository;
	private final EHRConnectionRepository ehrConnectionRepository;

	LinkEHRConnectionCommand(StudyRepository studyRepository, EHRConnectionRepository ehrConnectionRepository) {
		this.studyRepository = studyRepository;
		this.ehrConnectionRepository = ehrConnectionRepository;
	}

	@Override
	public void link(Request request) {
		assertStudyExists(request.getStudyId());
		EHRConnection ehrConnection = EHRConnection.newBuilder()
				.withStudyId(request.getStudyId())
				.withUri(request.getUri())
				.withSystem(request.getSystem())
				.build();
		ehrConnectionRepository.save(ehrConnection);
	}

	private void assertStudyExists(StudyId id) {
		studyRepository.getStudyById(id);
	}
}
