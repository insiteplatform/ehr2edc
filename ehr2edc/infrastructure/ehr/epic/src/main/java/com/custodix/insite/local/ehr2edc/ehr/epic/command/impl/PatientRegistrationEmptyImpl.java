package com.custodix.insite.local.ehr2edc.ehr.epic.command.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.ehr.epic.command.PatientRegistration;

public class PatientRegistrationEmptyImpl implements PatientRegistration {
	@Override
	public void register(@Valid @NotNull Request request) {
		//TODO: to be implemented see E2E-762
	}
}
