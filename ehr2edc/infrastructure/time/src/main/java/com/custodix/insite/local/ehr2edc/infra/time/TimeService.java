package com.custodix.insite.local.ehr2edc.infra.time;

import java.time.Instant;

import com.custodix.insite.local.ehr2edc.domain.service.Time;

public class TimeService implements Time {
	@Override
	public Instant now() {
		return Instant.now();
	}
}
