package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.GetSubject;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;

@Query
class GetSubjectQuery implements GetSubject {

	private StudyRepository studyRepository;
	private GetCurrentUser currentUser;

	GetSubjectQuery(StudyRepository studyRepository, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
	}

	@Override
	public Response getSubject(Request request) {
		return Response.newBuilder()
				.withSubject(mapSubject(studyRepository.findSubjectByIdAndStudyIdAndInvestigator(request.getSubjectId(),
						request.getStudyId(), currentUser.getUserId())))
				.build();
	}

	private Subject mapSubject(com.custodix.insite.local.ehr2edc.Subject subject) {
		return Subject.newBuilder()
				.withPatientId(subject.getPatientCDWReference())
				.withSubjectId(subject.getSubjectId())
				.withEdcSubjectReference(subject.getEdcSubjectReference())
				.withDateOfConsent(subject.getDateOfConsent())
				.withDateOfConsentWithdrawn(subject.getDateOfConsentWithdrawn())
				.withDataCaptureStopReason(subject.getDeregisterReason())
				.build();
	}
}
