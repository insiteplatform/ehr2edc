package com.custodix.insite.local.user.domain.events;

import com.custodix.insite.local.user.vocabulary.Email;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import eu.ehr4cr.workbench.local.AsyncSerializationSafeDomainEvent;

@JsonDeserialize(builder = PasswordRecoveredEvent.Builder.class)
public final class PasswordRecoveredEvent implements AsyncSerializationSafeDomainEvent {
	private final Email email;

	private PasswordRecoveredEvent(Builder builder) {
		email = builder.email;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Email getEmail() {
		return email;
	}

	@JsonPOJOBuilder
	public static final class Builder {
		private Email email;

		private Builder() {
		}

		public Builder withEmail(Email email) {
			this.email = email;
			return this;
		}

		public PasswordRecoveredEvent build() {
			return new PasswordRecoveredEvent(this);
		}
	}
}
