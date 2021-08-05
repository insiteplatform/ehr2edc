package com.custodix.insite.local.user;

import eu.ehr4cr.workbench.local.model.security.User;

public interface SendUserInviteMessage {
	void send(Request request);

	final class Request {
		private final User user;

		private Request(Builder builder) {
			user = builder.user;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public User getUser() {
			return user;
		}

		public static final class Builder {
			private User user;

			private Builder() {
			}

			public Builder withUser(User user) {
				this.user = user;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
