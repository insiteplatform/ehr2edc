package com.custodix.insite.local.ehr2edc.infra.time;

import java.time.Instant;

import com.custodix.insite.local.ehr2edc.domain.service.Time;

public class TestTimeService implements Time {

	private static final Instant NOW = Instant.now();
	private Instant now = NOW;

	@Override
	public Instant now() {
		return now;
	}

	public void reset() {
		now = NOW;
	}

	public void timeTravelTo(Instant date) {
		now = date;
	}
}