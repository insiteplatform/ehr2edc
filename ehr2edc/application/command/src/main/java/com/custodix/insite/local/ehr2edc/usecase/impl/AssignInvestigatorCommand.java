package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.AssignInvestigator;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.user.UserRepository;

@Command
class AssignInvestigatorCommand implements AssignInvestigator {

	private final StudyRepository studyRepository;
	private final UserRepository userRepository;

	AssignInvestigatorCommand(StudyRepository studyRepository, UserRepository userRepository) {
		this.studyRepository = studyRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void assign(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		study.assignInvestigator(request.getInvestigatorId(), userRepository);
		studyRepository.save(study);
	}
}