package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed;

public class FailSubjectRegistrationController {

	private final FailSubjectRegistration failSubjectRegistration;

	public FailSubjectRegistrationController(FailSubjectRegistration failSubjectRegistration) {
		this.failSubjectRegistration = failSubjectRegistration;
	}

	public void fail(ExportPatientFailed exportPatientFailed) {
		failSubjectRegistration.fail(toRequest(exportPatientFailed));
	}

	private FailSubjectRegistration.Request toRequest(ExportPatientFailed exportPatientFailed) {
		return FailSubjectRegistration.Request.newBuilder()
				.withPatientCDWReference(toPatientCDWReference(exportPatientFailed))
				.withSubjectId(SubjectId.of(exportPatientFailed.getSubjectId()))
				.build();
	}

	private PatientCDWReference toPatientCDWReference(ExportPatientFailed exportPatientFailed) {
		return PatientCDWReference.newBuilder()
				.withId(exportPatientFailed.getPatientId())
				.withSource(exportPatientFailed.getNamespace())
				.build();
	}
}
