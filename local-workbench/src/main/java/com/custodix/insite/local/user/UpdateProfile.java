package com.custodix.insite.local.user;

import eu.ehr4cr.workbench.local.security.annotation.HasUserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface UpdateProfile {
	void update(Request request);

	final class Request implements HasUserIdentifier {
		private final UserIdentifier userIdentifier;
		private final String username;
		private final String email;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
			username = builder.username;
			email = builder.email;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@Override
		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public String getUsername() {
			return username;
		}

		public String getEmail() {
			return email;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private String username;
			private String email;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withUsername(String username) {
				this.username = username;
				return this;
			}

			public Builder withEmail(String email) {
				this.email = email;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
