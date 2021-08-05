package com.custodix.insite.local.ehr2edc.usecase.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.UpdateEHRSubjectRegistrationStatus;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Command
public class UpdateEHRSubjectRegistrationStatusCommand implements UpdateEHRSubjectRegistrationStatus {
	private static final Logger LOGGER = getLogger(UpdateEHRSubjectRegistrationStatusCommand.class);

	@Autowired
	private StudyRepository studyRepository;

	@Override
	public void update(Request request) {
		Optional<Study> study = studyRepository.findBySubjectId(request.getSubjectId());

		if(study.isPresent()) {
			updateEHRSubjectRegistrationStatus(study.get(), request.getSubjectId(), request.getStatus());
		} else {
			LOGGER.warn("Cannot update EHR registration status for subject with id {}, cause the subject is not linked to any study ",
					request.getSubjectId().getId());
		}
	}

	private void updateEHRSubjectRegistrationStatus(Study study, SubjectId subjectId, EHRRegistrationStatus status) {
		study.updateEHRSubjectRegistrationStatus(subjectId, status);
		studyRepository.save(study);
	}
}
