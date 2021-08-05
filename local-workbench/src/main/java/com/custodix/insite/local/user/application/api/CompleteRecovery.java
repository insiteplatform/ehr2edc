package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;

import com.custodix.insite.local.user.vocabulary.Password;

public interface CompleteRecovery {
	void completeRecovery(@Valid Request request);

	final class Request {
		private final long userId;
		@Valid
		private final Password password;
		private final String tempPassword;

		private Request(Builder builder) {
			userId = builder.userId;
			password = builder.password;
			tempPassword = builder.tempPassword;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public Password getPassword() {
			return password;
		}

		public String getTempPassword() {
			return tempPassword;
		}

		public static final class Builder {
			private long userId;
			private Password password;
			private String tempPassword;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Builder withPassword(Password password) {
				this.password = password;
				return this;
			}

			public Builder withTempPassword(String tempPassword) {
				this.tempPassword = tempPassword;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
