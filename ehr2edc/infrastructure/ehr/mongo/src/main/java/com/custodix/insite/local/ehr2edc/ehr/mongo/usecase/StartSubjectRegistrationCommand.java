package com.custodix.insite.local.ehr2edc.ehr.mongo.usecase;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.domain.SubjectRegistration;

@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StartSubjectRegistrationCommand implements StartSubjectRegistration {
	@Override
	public void start(StartSubjectRegistration.Request request) {
		SubjectRegistration subjectRegistration = new SubjectRegistration(request.getSubjectId(),
				request.getPatientCDWReference());

		subjectRegistration.starts();
	}
}
