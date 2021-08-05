package com.custodix.insite.local.user.domain.events;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import eu.ehr4cr.workbench.local.AsyncSerializationSafeDomainEvent;

@JsonDeserialize(builder = ImminentlyExpiringPasswordEvent.Builder.class)
public final class ImminentlyExpiringPasswordEvent implements AsyncSerializationSafeDomainEvent {
	private final String userMailAddress;
	private final Date expirationDate;

	private ImminentlyExpiringPasswordEvent(Builder builder) {
		userMailAddress = builder.userMailAddress;
		expirationDate = builder.expirationDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getUserMailAddress() {
		return userMailAddress;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	@JsonPOJOBuilder
	public static final class Builder {
		private String userMailAddress;
		private Date expirationDate;

		private Builder() {
		}

		public Builder withUserMailAddress(String userMailAddress) {
			this.userMailAddress = userMailAddress;
			return this;
		}

		public Builder withExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
			return this;
		}

		public ImminentlyExpiringPasswordEvent build() {
			return new ImminentlyExpiringPasswordEvent(this);
		}
	}
}
