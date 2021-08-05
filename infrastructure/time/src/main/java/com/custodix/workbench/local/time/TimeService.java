package com.custodix.workbench.local.time;

import java.util.Date;

import eu.ehr4cr.workbench.local.service.Time;

public class TimeService implements Time {
	@Override
	public Date now() {
		return new Date();
	}
}
