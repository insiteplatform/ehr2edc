package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded;

public class SucceedSubjectRegistrationController {

	private final SucceedSubjectRegistration succeedSubjectRegistration;

	public SucceedSubjectRegistrationController(SucceedSubjectRegistration succeedSubjectRegistration) {
		this.succeedSubjectRegistration = succeedSubjectRegistration;
	}

	public void success(ExportPatientEnded exportPatientEnded) {
		succeedSubjectRegistration.succeed(toRequest(exportPatientEnded));
	}

	private SucceedSubjectRegistration.Request toRequest(ExportPatientEnded exportPatientEnded) {
		return SucceedSubjectRegistration.Request.newBuilder()
				.withPatientCDWReference(toPatientCDWReference(exportPatientEnded))
				.withSubjectId(SubjectId.of(exportPatientEnded.getSubjectId()))
				.build();
	}

	private PatientCDWReference toPatientCDWReference(ExportPatientEnded exportPatientEnded) {
		return PatientCDWReference.newBuilder()
				.withId(exportPatientEnded.getPatientId())
				.withSource(exportPatientEnded.getNamespace())
				.build();
	}
}
