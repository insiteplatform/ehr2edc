package com.custodix.insite.local.user.vocabulary;

import java.time.Duration;
import java.util.Date;

import javax.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;

public interface AuthenticationLockSettings {
	int getAttemptsMax();

	Duration getAttemptsInterval();

	Duration getInterval();

	Date getCutoffDate();

	Date getLockIntervalStart();

	@Validated
	final class Attempts {
		@Min(1)
		private int max;
		private Duration interval;

		Attempts() {
			// Configuration mapping
		}

		public Attempts(int max, Duration interval) {
			this.max = max;
			this.interval = interval;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

		public Duration getInterval() {
			return interval;
		}

		public void setInterval(Duration interval) {
			this.interval = interval;
		}
	}
}
