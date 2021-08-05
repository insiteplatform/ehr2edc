package com.custodix.insite.mongodb.export.patient.infrastructure.time;

import java.time.LocalDateTime;

import com.custodix.insite.mongodb.export.patient.domain.service.Time;

public class TimeService implements Time {
	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}
}
