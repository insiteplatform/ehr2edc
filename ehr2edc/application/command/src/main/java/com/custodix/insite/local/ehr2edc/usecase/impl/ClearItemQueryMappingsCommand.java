package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.ClearItemQueryMappings;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class ClearItemQueryMappingsCommand implements ClearItemQueryMappings {

	private final StudyRepository studyRepository;

	ClearItemQueryMappingsCommand(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@Override
	public void clear(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		study.clearItemQueryMappings();
		studyRepository.save(study);
	}
}
