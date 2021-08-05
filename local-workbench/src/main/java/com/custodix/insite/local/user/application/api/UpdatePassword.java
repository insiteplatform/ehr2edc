package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;

import com.custodix.insite.local.user.vocabulary.Password;

import eu.ehr4cr.workbench.local.security.annotation.HasUserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface UpdatePassword {
	void update(@Valid Request request);

	final class Request implements HasUserIdentifier {
		private final UserIdentifier userIdentifier;
		private final Password oldPassword;
		@Valid
		private final Password newPassword;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
			oldPassword = builder.oldPassword;
			newPassword = builder.newPassword;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@Override
		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public Password getOldPassword() {
			return oldPassword;
		}

		public Password getNewPassword() {
			return newPassword;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private Password oldPassword;
			private Password newPassword;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withOldPassword(Password oldPassword) {
				this.oldPassword = oldPassword;
				return this;
			}

			public Builder withNewPassword(Password newPassword) {
				this.newPassword = newPassword;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
