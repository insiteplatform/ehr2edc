package com.custodix.insite.local.user;

import java.util.List;

import eu.ehr4cr.workbench.local.model.security.UserRole;

public interface EditUserRoles {
	void editRoles(Request request);

	final class Request {
		private final long userId;
		private final List<UserRole> userRoles;

		private Request(Builder builder) {
			userId = builder.userId;
			userRoles = builder.userRoles;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public List<UserRole> getUserRoles() {
			return userRoles;
		}

		public static final class Builder {
			private long userId;
			private List<UserRole> userRoles;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Builder withUserRoles(List<UserRole> userRoles) {
				this.userRoles = userRoles;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}

	}
}
