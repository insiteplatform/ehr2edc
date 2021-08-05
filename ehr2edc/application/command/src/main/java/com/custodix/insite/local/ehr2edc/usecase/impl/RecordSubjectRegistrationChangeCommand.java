package com.custodix.insite.local.ehr2edc.usecase.impl;

import static com.custodix.insite.local.ehr2edc.RegistrationRecord.deregistration;
import static com.custodix.insite.local.ehr2edc.RegistrationRecord.registration;

import com.custodix.insite.local.ehr2edc.RegistrationRecord;
import com.custodix.insite.local.ehr2edc.RegistrationRecordRepository;
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class RecordSubjectRegistrationChangeCommand implements RecordSubjectRegistrationChange {

	private RegistrationRecordRepository registrationRecordRepository;

	RecordSubjectRegistrationChangeCommand(RegistrationRecordRepository registrationRecordRepository) {
		this.registrationRecordRepository = registrationRecordRepository;
	}

	@Override
	public void register(RegistrationRequest request) {
		RegistrationRecord registrationRecord = registration(request.getPatientId(), request.getStudyId(),
				request.getSubjectId(), request.getDate());
		registrationRecordRepository.save(registrationRecord);
	}

	@Override
	public void deregister(DeregistrationRequest request) {
		RegistrationRecord deregistrationRecord = deregistration(request.getPatientId(), request.getStudyId(),
				request.getSubjectId(), request.getDate(), request.getReason());
		registrationRecordRepository.save(deregistrationRecord);
	}
}
