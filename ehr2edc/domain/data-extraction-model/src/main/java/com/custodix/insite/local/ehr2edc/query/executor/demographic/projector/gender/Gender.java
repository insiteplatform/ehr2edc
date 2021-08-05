package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender;

import java.util.Arrays;

public enum Gender {
	MALE("male", "M"),
	FEMALE("female", "F"),
	UNKNOWN("unknown", "U");

	private String code;
	private String sdtmCode;

	Gender(final String code, final String sdtmCode) {
		this.code = code;
		this.sdtmCode = sdtmCode;
	}

	public String getSdtmCode() {
		return sdtmCode;
	}

	public static Gender fromCode(String code) {
		return Arrays.stream(Gender.values())
				.filter(race -> race.code.equals(code))
				.findFirst()
				.orElse(Gender.UNKNOWN);
	}
}
