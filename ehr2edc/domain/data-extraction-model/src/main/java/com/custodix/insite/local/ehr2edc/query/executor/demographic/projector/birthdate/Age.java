package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.time.LocalDate;

public class Age {

	private static final int HOURS_PER_DAY = 24;

	private final LocalDate start;
	private final LocalDate end;
	private final AgeUnit unit;

	public Age(LocalDate start, LocalDate end, AgeUnit unit) {
		this.start = start;
		this.end = end;
		this.unit = unit;
	}

	public long numerical() {
		return inUnits(unit);
	}

	public String unit() {
		return unit.name();
	}

	private long inUnits(AgeUnit ageUnit) {
		if (ageUnit == AgeUnit.HOURS) {
			// ChronoUnit HOURS is not supported by LocalDate.between(), since the highest resolution of the data is days.
			return inUnits(AgeUnit.DAYS) * HOURS_PER_DAY;
		}
		return ageUnit.getUnit()
				.between(start, end);
	}
}
