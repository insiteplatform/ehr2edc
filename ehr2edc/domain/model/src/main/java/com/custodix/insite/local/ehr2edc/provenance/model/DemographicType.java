package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Arrays;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public enum DemographicType {
	BIRTH_DATE() {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType type) {
			return type
					== com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.BIRTH_DATE;
		}
	},
	DEATH_DATE() {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType type) {
			return type
					== com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.DEATH_DATE;
		}
	},
	VITAL_STATUS() {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType type) {
			return type
					== com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.VITAL_STATUS;
		}
	},
	GENDER() {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType type) {
			return type == com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.GENDER;
		}
	};

	public static DemographicType from(
			com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType demographicType) {
		return Arrays.stream(values())
				.filter(v -> v.matches(demographicType))
				.findFirst()
				.orElseThrow(() -> new SystemException("No mapping found for " + demographicType));
	}

	abstract boolean matches(
			com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType demographicType);
}
