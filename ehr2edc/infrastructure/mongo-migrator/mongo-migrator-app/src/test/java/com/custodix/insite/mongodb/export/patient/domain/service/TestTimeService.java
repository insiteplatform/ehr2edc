package com.custodix.insite.mongodb.export.patient.domain.service;

import java.time.LocalDateTime;

public class TestTimeService implements Time {
	private static final LocalDateTime NOW = LocalDateTime.now();
	private LocalDateTime now = NOW;

	@Override
	public LocalDateTime now() {
		return now;
	}

	public void reset() {
		now = NOW;
	}

	public void timeTravelTo(LocalDateTime date) {
		now = date;
	}

}