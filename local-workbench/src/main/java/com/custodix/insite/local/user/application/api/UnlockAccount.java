package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.user.vocabulary.Email;

public interface UnlockAccount {
	void unlockAccount(@Valid Request request);

	final class Request {
		@NotNull
		@Valid
		private final Email email;

		private Request(Builder builder) {
			email = builder.email;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Email getEmail() {
			return email;
		}

		public static final class Builder {
			private Email email;

			private Builder() {
			}

			public Builder withEmail(Email email) {
				this.email = email;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
