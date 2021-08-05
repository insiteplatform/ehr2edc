package eu.ehr4cr.workbench.local.service;

import java.util.Date;

public final class DomainTime {
	private static Time time;

	private DomainTime() {
		// Class is not meant to be an object
	}

	public static Date now() {
		return time.now();
	}

	public static void setTime(Time time) {
		DomainTime.time = time;
	}
}
