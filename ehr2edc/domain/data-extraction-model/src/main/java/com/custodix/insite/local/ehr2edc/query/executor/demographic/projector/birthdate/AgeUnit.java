package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

public enum AgeUnit {
	DAYS("d", ChronoUnit.DAYS),
	HOURS("h", ChronoUnit.HOURS),
	MONTHS("mo", ChronoUnit.MONTHS),
	WEEKS("wk", ChronoUnit.WEEKS),
	YEARS("a", ChronoUnit.YEARS),
	;

	private final String code;
	private final ChronoUnit unit;

	AgeUnit(final String code, final ChronoUnit unit) {
		this.code = code;
		this.unit = unit;
	}

	public String getCode() {
		return code;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

	public static Optional<AgeUnit> fromCode(String code) {
		return Arrays.stream(AgeUnit.values())
				.filter(ageUnit -> ageUnit.getCode()
						.equals(code))
				.findFirst();
	}

}
