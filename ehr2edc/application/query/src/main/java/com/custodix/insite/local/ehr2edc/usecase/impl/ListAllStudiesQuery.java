package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListAllStudies;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Query
class ListAllStudiesQuery implements ListAllStudies {

	private final StudyRepository studyRepository;
	private final GetCurrentUser getCurrentUser;

	ListAllStudiesQuery(StudyRepository studyRepository, GetCurrentUser getCurrentUser) {
		this.studyRepository = studyRepository;
		this.getCurrentUser = getCurrentUser;
	}

	@Override
	public Response allStudies() {
		UserIdentifier user = getCurrentUser.getUserId();
		return studyRepository.findAll()
				.stream()
				.filter(isDrm().or(isAssignedInvestigator(user)))
				.map(this::toStudy)
				.collect(collectingAndThen(toList(), this::toResponse));
	}

	private Predicate<com.custodix.insite.local.ehr2edc.Study> isDrm() {
		return o -> getCurrentUser.isDRM();
	}

	private Predicate<com.custodix.insite.local.ehr2edc.Study> isAssignedInvestigator(UserIdentifier user) {
		return study -> study.hasInvestigatorFor(user);
	}

	private Study toStudy(com.custodix.insite.local.ehr2edc.Study study) {
		return ListAllStudies.Study.newBuilder()
				.withId(study.getStudyId()
						.getId())
				.withName(study.getName())
				.withDescription(study.getDescription())
				.build();
	}

	private Response toResponse(List<Study> studies) {
		return Response.newBuilder(studies)
				.build();
	}
}
