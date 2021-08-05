package com.custodix.insite.mongodb.export.patient.domain.model;

import java.time.LocalDateTime;

import com.custodix.insite.mongodb.export.patient.domain.service.Time;

public final class DomainTime {
	private static Time time;

	private DomainTime() {
		// Class is not meant to be an object
	}

	public static LocalDateTime now() {
		return time.now();
	}

	public static void setTime(Time time) {
		DomainTime.time = time;
	}
}
