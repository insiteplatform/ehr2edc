package com.custodix.insite.local.user.domain;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import com.custodix.insite.local.user.vocabulary.AuthenticationAttemptIdentifier;
import com.custodix.insite.local.user.vocabulary.Email;

public final class AuthenticationAttempt {
	private AuthenticationAttemptIdentifier identifier;
	private Email email;
	private AuthenticationAttemptResult result;
	private Date timestamp;

	private AuthenticationAttempt(Builder builder) {
		identifier = builder.identifier;
		email = builder.email;
		result = builder.result;
		timestamp = builder.timestamp;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Optional<AuthenticationAttemptIdentifier> getIdentifier() {
		return Optional.ofNullable(identifier);
	}

	public Email getEmail() {
		return email;
	}

	public AuthenticationAttemptResult getResult() {
		return result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public boolean isSuccessful() {
		return result.isSuccessState();
	}

	public boolean isAfter(AuthenticationAttempt authenticationAttempt) {
		return isAfter(authenticationAttempt.getTimestamp());
	}

	public boolean isAfter(Date date) {
		return timestamp.after(date);
	}

	public Duration getDurationBetween(AuthenticationAttempt authenticationAttempt) {
		return Duration.between(timestamp.toInstant(), authenticationAttempt.getTimestamp()
				.toInstant());
	}

	public static final class Builder {
		private AuthenticationAttemptIdentifier identifier;
		private Email email;
		private AuthenticationAttemptResult result;
		private Date timestamp;

		private Builder() {
		}

		public Builder withIdentifier(AuthenticationAttemptIdentifier identifier) {
			this.identifier = identifier;
			return this;
		}

		public Builder withEmail(Email email) {
			this.email = email;
			return this;
		}

		public Builder withResult(AuthenticationAttemptResult result) {
			this.result = result;
			return this;
		}

		public Builder withTimestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public AuthenticationAttempt build() {
			return new AuthenticationAttempt(this);
		}
	}
}
