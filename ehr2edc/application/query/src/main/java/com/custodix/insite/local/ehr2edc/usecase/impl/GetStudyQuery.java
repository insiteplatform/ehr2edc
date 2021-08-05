package com.custodix.insite.local.ehr2edc.usecase.impl;

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.SUBMIT_EVENT;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.GetStudy;
import com.custodix.insite.local.ehr2edc.query.InvestigatorProjection;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;

@Query
class GetStudyQuery implements GetStudy {
	private final StudyRepository studyRepository;
	private final StudyConnectionRepository studyConnectionRepository;
	private final GetCurrentUser getCurrentUser;

	GetStudyQuery(StudyRepository studyRepository, StudyConnectionRepository studyConnectionRepository,
			GetCurrentUser getCurrentUser) {
		this.studyRepository = studyRepository;
		this.studyConnectionRepository = studyConnectionRepository;
		this.getCurrentUser = getCurrentUser;
	}

	@Override
	public Response getStudy(Request request) {
		com.custodix.insite.local.ehr2edc.Study existingStudy = studyRepository.getStudyById(request.getStudyId());
		return Response.newBuilder()
				.withStudy(Study.newBuilder()
						.withName(existingStudy.getName())
						.withId(existingStudy.getStudyId())
						.withDescription(existingStudy.getDescription())
						.withInvestigators(toInvestigators(existingStudy))
						.withSubjects(mapSubjects(existingStudy))
						.withPermissions(mapPermissions(existingStudy))
						.build())
				.build();
	}

	private List<Subject> mapSubjects(com.custodix.insite.local.ehr2edc.Study existingStudy) {
		if (currentUserIsAssignedInvestigator(existingStudy)) {
			return existingStudy.getActiveSubjects()
					.stream()
					.map(mapSubject())
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private Function<SubjectProjection, Subject> mapSubject() {
		return subject -> GetStudy.Subject.newBuilder()
				.withSubjectId(subject.getSubjectId())
				.withEdcSubjectReference(subject.getEdcSubjectReference())
				.withPatientId(subject.getPatientCDWReference())
				.withConsentDateTime(subject.getDateOfConsent())
				.build();
	}

	private Collection<Investigator> toInvestigators(com.custodix.insite.local.ehr2edc.Study existingStudy) {
		return existingStudy.getInvestigators()
				.map(this::toInvestigators);
	}

	private Investigator toInvestigators(InvestigatorProjection investigatorProjection) {
		return Investigator.newBuilder()
				.withId(investigatorProjection.getInvestigatorId())
				.withName(investigatorProjection.getName())
				.withRemovable(getCurrentUser.isDRM())
				.build();
	}

	private Permissions mapPermissions(com.custodix.insite.local.ehr2edc.Study existingStudy) {
		boolean currentUserIsAssignedInvestigator = currentUserIsAssignedInvestigator(existingStudy);
		boolean writableEDCAvailable = studyConnectionRepository.isEnabled(existingStudy.getStudyId(), SUBMIT_EVENT);
		return Permissions.newBuilder()
				.withCanSubjectsBeAdded(currentUserIsAssignedInvestigator)
				.withCanSubjectsBeViewed(currentUserIsAssignedInvestigator)
				.withCanSendToEDC(writableEDCAvailable)
				.build();
	}

	private boolean currentUserIsAssignedInvestigator(com.custodix.insite.local.ehr2edc.Study existingStudy) {
		return existingStudy.hasInvestigatorFor(getCurrentUser.getUserId());
	}
}
