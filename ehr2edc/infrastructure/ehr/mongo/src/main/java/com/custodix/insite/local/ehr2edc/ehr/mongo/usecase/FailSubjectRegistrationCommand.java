package com.custodix.insite.local.ehr2edc.ehr.mongo.usecase;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.domain.SubjectRegistration;
import com.custodix.insite.mongodb.export.patient.application.annotation.Command;

@Command
public class FailSubjectRegistrationCommand implements FailSubjectRegistration {
	@Override
	public void fail(Request request) {
		SubjectRegistration subjectRegistration = new SubjectRegistration(request.getSubjectId(),
				request.getPatientCDWReference());

		subjectRegistration.fails();
	}
}
