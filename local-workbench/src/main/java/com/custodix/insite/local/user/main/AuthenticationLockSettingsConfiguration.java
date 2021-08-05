package com.custodix.insite.local.user.main;

import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import java.time.Duration;
import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;

@Validated
@ConfigurationProperties(prefix = "user.authentication.lock")
@Component
class AuthenticationLockSettingsConfiguration implements AuthenticationLockSettings {
	private Attempts attempts = new Attempts(3, Duration.ofMinutes(15));
	private Duration interval = Duration.ofMinutes(15);

	@Override
	public int getAttemptsMax() {
		return attempts.getMax();
	}

	@Override
	public Duration getAttemptsInterval() {
		return attempts.getInterval();
	}

	@Override
	public Duration getInterval() {
		return interval;
	}

	public void setAttempts(Attempts attempts) {
		this.attempts = attempts;
	}

	public void setInterval(Duration interval) {
		this.interval = interval;
	}

	@Override
	public Date getCutoffDate() {
		return Date.from(getLockIntervalStart().toInstant()
				.minus(getAttemptsInterval()));
	}

	@Override
	public Date getLockIntervalStart() {
		return Date.from(now().toInstant()
				.minus(getInterval()));
	}
}
