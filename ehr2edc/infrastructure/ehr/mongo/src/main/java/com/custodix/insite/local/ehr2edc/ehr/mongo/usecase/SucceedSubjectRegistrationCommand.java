package com.custodix.insite.local.ehr2edc.ehr.mongo.usecase;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.domain.SubjectRegistration;
import com.custodix.insite.mongodb.export.patient.application.annotation.Command;

@Command
public class SucceedSubjectRegistrationCommand implements SucceedSubjectRegistration {
	@Override
	public void succeed(Request request) {
		SubjectRegistration subjectRegistration = new SubjectRegistration(request.getSubjectId(),
				request.getPatientCDWReference());

		subjectRegistration.succeed();
	}
}
