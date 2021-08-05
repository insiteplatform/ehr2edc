package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListSubjects;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Query
class ListSubjectsQuery implements ListSubjects {

	private StudyRepository studyRepository;
	private GetCurrentUser currentUser;

	ListSubjectsQuery(StudyRepository studyRepository, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
	}

	@Override
	public Response list(Request request) {
		Study study = studyRepository.findStudyByIdAndInvestigator(request.getStudyId(), currentUser.getUserId());
		return Response.newBuilder()
				.withSubjects(activeSubjectIds(study))
				.build();
	}

	private List<SubjectId> activeSubjectIds(Study study) {
		return study.getActiveSubjects()
				.stream()
				.map(SubjectProjection::getSubjectId)
				.collect(Collectors.toList());
	}
}
