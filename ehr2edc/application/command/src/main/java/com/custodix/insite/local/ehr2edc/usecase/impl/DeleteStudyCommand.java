package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.DeleteStudy;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class DeleteStudyCommand implements DeleteStudy {
	private final StudyRepository studyRepository;

	DeleteStudyCommand(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@Override
	public void delete(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		studyRepository.delete(study);
	}
}
