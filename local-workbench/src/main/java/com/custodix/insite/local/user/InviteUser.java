package com.custodix.insite.local.user;

import java.util.List;

import eu.ehr4cr.workbench.local.model.security.UserRole;

public interface InviteUser {
	void invite(Request request);

	final class Request {
		private final String email;
		private final String username;
		private final List<UserRole> roles;

		private Request(Builder builder) {
			email = builder.email;
			username = builder.username;
			roles = builder.roles;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public String getEmail() {
			return email;
		}

		public String getUsername() {
			return username;
		}

		public List<UserRole> getRoles() {
			return roles;
		}

		public static final class Builder {
			private String email;
			private String username;
			private List<UserRole> roles;

			private Builder() {
			}

			public Builder withEmail(String email) {
				this.email = email;
				return this;
			}

			public Builder withUsername(String username) {
				this.username = username;
				return this;
			}

			public Builder withRoles(List<UserRole> roles) {
				this.roles = roles;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
