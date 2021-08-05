package com.custodix.insite.local.ehr2edc.query.executor.demographic.domain;

public enum DemographicType {
	BIRTH_DATE("dateOfBirth"),
	DEATH_DATE("dateOfDeath"),
	VITAL_STATUS("vitalStatus"),
	GENDER("gender");

	String code;

	DemographicType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}


