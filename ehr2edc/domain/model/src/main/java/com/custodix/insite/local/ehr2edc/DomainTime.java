package com.custodix.insite.local.ehr2edc;

import java.time.Instant;

import com.custodix.insite.local.ehr2edc.domain.service.Time;

public final class DomainTime {
	private static Time time;

	private DomainTime() {
		// Class is not meant to be an object
	}

	public static Instant now() {
		return time.now();
	}

	public static void setTime(Time time) {
		DomainTime.time = time;
	}
}
