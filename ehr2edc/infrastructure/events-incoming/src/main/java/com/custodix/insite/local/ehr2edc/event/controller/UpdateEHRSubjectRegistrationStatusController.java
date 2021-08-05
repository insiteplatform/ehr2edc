package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.command.UpdateEHRSubjectRegistrationStatus;
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus;
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class UpdateEHRSubjectRegistrationStatusController {

	private UpdateEHRSubjectRegistrationStatus updateEHRSubjectRegistrationStatus;

	public UpdateEHRSubjectRegistrationStatusController(
			UpdateEHRSubjectRegistrationStatus updateEHRSubjectRegistrationStatus) {
		this.updateEHRSubjectRegistrationStatus = updateEHRSubjectRegistrationStatus;
	}

	public void handle(EHRSubjectRegistrationStatusUpdated ehrSubjectRegistrationStatusUpdated) {
		updateEHRSubjectRegistrationStatus.update(toRequest(ehrSubjectRegistrationStatusUpdated));
	}

	private UpdateEHRSubjectRegistrationStatus.Request toRequest(
			EHRSubjectRegistrationStatusUpdated ehrSubjectRegistrationStatusUpdated) {
		return  UpdateEHRSubjectRegistrationStatus.Request.newBuilder()
				.withPatientCDWReference(toPatientReference(ehrSubjectRegistrationStatusUpdated))
				.withSubjectId(SubjectId.of(ehrSubjectRegistrationStatusUpdated.getSubjectId()))
				.withStatus(toEHRSubjectRegistrationStatus(ehrSubjectRegistrationStatusUpdated.getStatus()))
				.build();
	}

	private EHRRegistrationStatus toEHRSubjectRegistrationStatus(EHRSubjectRegistrationStatus status) {
		if(isRegistered(status)) {
			return EHRRegistrationStatus.REGISTERED;
		} else if(isNotRegistered(status)) {
			return EHRRegistrationStatus.NOT_REGISTERED;
		} else {
			throw new SystemException(String.format("Cannot convert status of 'EHRSubjectRegistrationStatus' event '%s' to an EHR2EDC status", status));
		}
	}

	private boolean isNotRegistered(EHRSubjectRegistrationStatus status) {
		return EHRSubjectRegistrationStatus.FAILED.equals(status) ||  EHRSubjectRegistrationStatus.STARTED.equals(status);

	}

	private boolean isRegistered(EHRSubjectRegistrationStatus status) {
		return EHRSubjectRegistrationStatus.REGISTERED.equals(status);
	}

	private PatientCDWReference toPatientReference(
			EHRSubjectRegistrationStatusUpdated ehrSubjectRegistrationStatusUpdated) {
		return PatientCDWReference.newBuilder()
				.withSource(ehrSubjectRegistrationStatusUpdated.getNamespace())
				.withId(ehrSubjectRegistrationStatusUpdated.getPatientId())
				.build();
	}
}
