package com.custodix.insite.local.user.main;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

@ConfigurationProperties(prefix = "user.password.expiration")
@Component
class PasswordExpirySettingsConfiguration implements PasswordExpirySettings {
	private boolean enabled;
	private ExpiryDuration duration;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Duration getImminentDuration() {
		return duration.getImminent();
	}

	@Override
	public Duration getExpiryDuration() {
		return duration.getExpiry();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDuration(ExpiryDuration duration) {
		this.duration = duration;
	}
}
