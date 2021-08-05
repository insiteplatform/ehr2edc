package com.custodix.insite.local.ehr2edc.ehr.mongo.domain;

import static com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus.FAILED;
import static com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus.REGISTERED;
import static com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus.STARTED;
import static com.custodix.insite.local.ehr2edc.ehr.main.domain.event.DomainEventPublisher.publishEvent;

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectRegistration {

	private final PatientCDWReference patientCDWReference;
	private final SubjectId subjectId;

	public SubjectRegistration(SubjectId subjectId, PatientCDWReference patientCDWReference) {
		this.patientCDWReference = patientCDWReference;
		this.subjectId = subjectId;
	}

	public void fails() {
		publishEvent(createEHRSubjectRegistrationFailedEvent());
	}

	public void starts() {
		publishEvent(createEHRSubjectRegistrationStartedEvent());
	}

	public void succeed() {
		publishEvent(createEHRSubjectRegistrationSucceedEvent());
	}

	private EHRSubjectRegistrationStatusUpdated createEHRSubjectRegistrationFailedEvent() {
		return EHRSubjectRegistrationStatusUpdated.newBuilder()
				.withSubjectId(subjectId.getId())
				.withNamespace(patientCDWReference.getSource())
				.withPatientId(patientCDWReference.getId())
				.withStatus(FAILED)
				.build();
	}

	private EHRSubjectRegistrationStatusUpdated createEHRSubjectRegistrationStartedEvent() {
		return EHRSubjectRegistrationStatusUpdated.newBuilder()
				.withSubjectId(subjectId.getId())
				.withNamespace(patientCDWReference.getSource())
				.withPatientId(patientCDWReference.getId())
				.withStatus(STARTED)
				.build();
	}

	private EHRSubjectRegistrationStatusUpdated createEHRSubjectRegistrationSucceedEvent() {
		return EHRSubjectRegistrationStatusUpdated.newBuilder()
				.withSubjectId(subjectId.getId())
				.withNamespace(patientCDWReference.getSource())
				.withPatientId(patientCDWReference.getId())
				.withStatus(REGISTERED)
				.build();
	}
}
