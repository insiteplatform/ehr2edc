package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListAvailableStudies;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Query
class ListAvailableStudiesQuery implements ListAvailableStudies {

	private StudyRepository studyRepository;
	private GetCurrentUser currentUser;

	ListAvailableStudiesQuery(StudyRepository studyRepository, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
	}

	@Override
	public Response availableStudies(Request request) {
		UserIdentifier investigatorId = currentUser.getUserId();
		return Response.newBuilder()
				.withAvailableStudies(mapToStudies(
						studyRepository.findAvailableStudiesForPatient(request.getPatientCDWReference(),
								investigatorId)))
				.build();
	}

	private List<Study> mapToStudies(List<com.custodix.insite.local.ehr2edc.Study> availableStudiesForPatient) {
		return availableStudiesForPatient.stream()
				.map(this::mapToStudy)
				.collect(Collectors.toList());
	}

	private Study mapToStudy(com.custodix.insite.local.ehr2edc.Study study) {
		return Study.newBuilder()
				.withStudyId(study.getStudyId())
				.withName(study.getName())
				.withDescription(study.getDescription())
				.build();
	}
}
