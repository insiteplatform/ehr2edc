package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Arrays;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public enum FastingStatus {
	FASTING() {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus status) {
			return status == com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.FASTING;
		}
	},
	NOT_FASTING {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus status) {
			return status == com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.NOT_FASTING;
		}
	},
	UNDEFINED {
		@Override
		boolean matches(com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus status) {
			return status == com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.UNDEFINED;
		}
	};

	public static FastingStatus from(
			com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus status) {
		return Arrays.stream(values())
				.filter(v -> v.matches(status))
				.findFirst()
				.orElseThrow(() -> new SystemException("No mapping found for " + status));
	}

	abstract boolean matches(com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus status);
}
