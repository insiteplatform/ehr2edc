package com.custodix.insite.local.user.vocabulary;

import java.time.Duration;

public interface PasswordExpirySettings {
	boolean isEnabled();

	Duration getImminentDuration();

	Duration getExpiryDuration();

	final class ExpiryDuration {
		private Duration imminent;
		private Duration expiry;

		public Duration getImminent() {
			return imminent;
		}

		public void setImminent(Duration imminent) {
			this.imminent = imminent;
		}

		public Duration getExpiry() {
			return expiry;
		}

		public void setExpiry(Duration expiry) {
			this.expiry = expiry;
		}
	}
}
