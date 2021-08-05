package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting;

public class StartSubjectRegistrationController {

	private final StartSubjectRegistration startSubjectRegistration;

	public StartSubjectRegistrationController(StartSubjectRegistration startSubjectRegistration) {
		this.startSubjectRegistration = startSubjectRegistration;
	}

	public void start(ExportPatientStarting exportPatientStarting) {
		startSubjectRegistration.start(toRequest(exportPatientStarting));
	}

	private StartSubjectRegistration.Request toRequest(ExportPatientStarting exportPatientStarting) {
		return StartSubjectRegistration.Request.newBuilder()
				.withPatientCDWReference(toPatientCDWReference(exportPatientStarting))
				.withSubjectId(SubjectId.of(exportPatientStarting.getSubjectId()))
				.build();
	}

	private PatientCDWReference toPatientCDWReference(ExportPatientStarting exportPatientStarting) {
		return PatientCDWReference.newBuilder()
				.withId(exportPatientStarting.getPatientId())
				.withSource(exportPatientStarting.getNamespace())
				.build();
	}

}
