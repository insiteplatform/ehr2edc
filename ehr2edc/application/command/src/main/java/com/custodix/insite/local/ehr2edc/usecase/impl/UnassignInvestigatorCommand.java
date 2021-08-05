package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.UnassignInvestigator;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class UnassignInvestigatorCommand implements UnassignInvestigator {

	private StudyRepository studyRepository;

	UnassignInvestigatorCommand(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@Override
	public void unassign(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		study.unassignInvestigator(request.getInvestigatorId());
		studyRepository.save(study);
	}
}
