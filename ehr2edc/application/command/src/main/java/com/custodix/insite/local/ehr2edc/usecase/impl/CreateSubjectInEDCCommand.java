package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.EDCStudyGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.CreateSubjectInEDC;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class CreateSubjectInEDCCommand implements CreateSubjectInEDC {

	private final EDCStudyGateway edcGateway;
	private final StudyRepository studyRepository;

	CreateSubjectInEDCCommand(EDCStudyGateway edcGateway, StudyRepository studyRepository) {
		this.edcGateway = edcGateway;
		this.studyRepository = studyRepository;
	}

	@Override
	public void create(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		edcGateway.createSubject(study, request.getEdcSubjectReference());
	}

}

