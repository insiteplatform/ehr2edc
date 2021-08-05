package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListRegisteredStudies;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Query
class ListRegisteredStudiesQuery implements ListRegisteredStudies {

	private StudyRepository studyRepository;
	private GetCurrentUser currentUser;

	ListRegisteredStudiesQuery(StudyRepository studyRepository, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
	}

	@Override
	public Response registeredStudies(Request request) {
		UserIdentifier investigatorId = currentUser.getUserId();
		PatientCDWReference patientCDWReference = request.getPatientCDWReference();
		List<com.custodix.insite.local.ehr2edc.Study> registeredStudiesForPatient = studyRepository.findRegisteredStudiesForPatient(
				patientCDWReference, investigatorId);
		return Response.newBuilder()
				.withRegisteredStudies(mapToStudies(registeredStudiesForPatient, patientCDWReference))
				.build();
	}

	private List<Study> mapToStudies(List<com.custodix.insite.local.ehr2edc.Study> registeredStudiesForPatient,
			PatientCDWReference patientCDWReference) {
		return registeredStudiesForPatient.stream()
				.map(s -> mapToStudy(s, patientCDWReference))
				.collect(Collectors.toList());
	}

	private Study mapToStudy(com.custodix.insite.local.ehr2edc.Study study, PatientCDWReference patientCDWReference) {
		return Study.newBuilder()
				.withStudyId(study.getStudyId())
				.withName(study.getName())
				.withDescription(study.getDescription())
				.withSubject(retrieveSubjectThatsQueried(study.getSubject(patientCDWReference)))
				.build();
	}

	private Subject retrieveSubjectThatsQueried(SubjectProjection subject) {
		return Subject.newBuilder()
				.withPatientCDWReference(subject.getPatientCDWReference())
				.withSubjectId(subject.getSubjectId())
				.withEdcSubjectReference(subject.getEdcSubjectReference())
				.withDateOfConsent(subject.getDateOfConsent())
				.build();
	}
}
