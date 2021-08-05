package com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum FastingStatus {
	FASTING {
		@Override
		public Optional<Boolean> isFasting() {
			return Optional.of(true);
		}
	},
	NOT_FASTING {
		@Override
		public Optional<Boolean> isFasting() {
			return Optional.of(false);
		}
	},
	UNDEFINED {
		@Override
		public Optional<Boolean> isFasting() {
			return Optional.empty();
		}
	};

	public abstract Optional<Boolean> isFasting();

	public static FastingStatus fromValue(String fastingStatus) {
		if(StringUtils.isBlank(fastingStatus)) {
			return FastingStatus.UNDEFINED;
		} else {
			return FastingStatus.valueOf(fastingStatus);
		}
	}
}
