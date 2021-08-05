package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.Study.DeregisterSubjectData;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.DeregisterSubject;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;

@Command
class DeregisterSubjectCommand implements DeregisterSubject {

	private StudyRepository studyRepository;
	private GetCurrentUser currentUser;

	DeregisterSubjectCommand(StudyRepository studyRepository, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
	}

	@Override
	public void deregister(Request request) {
		Study study = studyRepository.findStudyByIdAndInvestigator(request.getStudyId(), currentUser.getUserId());

		study.deregister(deregisterSubjectData(request));

		studyRepository.save(study);
	}

	private DeregisterSubjectData deregisterSubjectData(Request request) {
		return new DeregisterSubjectData(request.getStudyId(), request.getSubjectId(), request.getEndDate(),
				request.getDataCaptureStopReason());
	}
}
