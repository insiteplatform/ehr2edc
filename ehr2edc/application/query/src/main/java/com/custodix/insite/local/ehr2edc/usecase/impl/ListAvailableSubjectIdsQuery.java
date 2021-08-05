package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.List;

import com.custodix.insite.local.ehr2edc.EDCStudyGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListAvailableSubjectIds;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;

@Query
class ListAvailableSubjectIdsQuery implements ListAvailableSubjectIds {

	private final StudyRepository studyRepository;
	private final EDCStudyGateway edcStudyGateway;
	private final GetCurrentUser currentUser;

	ListAvailableSubjectIdsQuery(StudyRepository studyRepository,
			EDCStudyGateway edcStudyGateway, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.edcStudyGateway = edcStudyGateway;
		this.currentUser = currentUser;
	}

	@Override
	public Response list(Request request) {
		Study study = studyRepository.findStudyByIdAndInvestigator(request.getStudyId(), currentUser.getUserId());

		EDCStudyGateway.RegisteredSubjects candidateSubjectIds = edcStudyGateway.findRegisteredSubjectIds(study.getStudyId());
		List<EDCSubjectReference> availableSubjectIds = study.listAvailableSubjectIds(candidateSubjectIds.getRegisteredSubjectReferences());
		return Response.newBuilder()
				.withFromEDC(candidateSubjectIds.isFromEDC())
				.withSubjectIds(availableSubjectIds)
				.build();
	}
}
