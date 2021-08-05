package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.EDCStudyGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.Study.RegisterServices;
import com.custodix.insite.local.ehr2edc.Study.SubjectRegistrationData;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.RegisterSubject;
import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Command
class RegisterSubjectCommand implements RegisterSubject {

	private final StudyRepository studyRepository;
	private final GetCurrentUser currentUser;
	private final PatientEHRGateway patientEHRGateway;
	private final EDCStudyGateway edcStudyGateway;

	RegisterSubjectCommand(StudyRepository studyRepository, GetCurrentUser currentUser, PatientEHRGateway patientEHRGateway,
			EDCStudyGateway edcStudyGateway) {
		this.studyRepository = studyRepository;
		this.currentUser = currentUser;
		this.patientEHRGateway = patientEHRGateway;
		this.edcStudyGateway = edcStudyGateway;
	}

	@Override
	public Response register(Request request) {
		final Study study = studyRepository.findStudyByIdAndInvestigator(request.getStudyId(), currentUser.getUserId());
		final SubjectId subjectId = study.register(registerSubjectData(request), registrationServices());
		studyRepository.save(study);
		return Response.newBuilder().withSubjectId(subjectId).build();
	}

	private SubjectRegistrationData registerSubjectData(Request request) {
		return new SubjectRegistrationData(request.getPatientId(), request.getStudyId(),
				request.getEdcSubjectReference(), request.getDateOfConsent(), request.getLastName(), request.getFirstName(), request.getBirthDate());
	}

	private RegisterServices registrationServices() {
		return new RegisterServices(patientEHRGateway, edcStudyGateway);
	}
}
