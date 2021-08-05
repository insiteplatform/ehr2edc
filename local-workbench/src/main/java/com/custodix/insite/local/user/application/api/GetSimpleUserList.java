package com.custodix.insite.local.user.application.api;

import java.util.Collections;
import java.util.List;

import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface GetSimpleUserList {
	Response getActiveUsers();

	final class Response {
		private final List<User> users;

		private Response(Builder builder) {
			users = builder.users;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.users = copy.getUsers();
			return builder;
		}

		public List<User> getUsers() {
			return Collections.unmodifiableList(users);
		}

		public static final class Builder {
			private List<User> users;

			private Builder() {
			}

			public Builder withUsers(List<User> users) {
				this.users = users;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class User {
		private final UserIdentifier id;
		private final String name;

		private User(Builder builder) {
			id = builder.id;
			name = builder.name;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(User copy) {
			Builder builder = new Builder();
			builder.id = copy.getId();
			builder.name = copy.getName();
			return builder;
		}

		public UserIdentifier getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public static final class Builder {
			private UserIdentifier id;
			private String name;

			private Builder() {
			}

			public Builder withId(UserIdentifier id) {
				this.id = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public User build() {
				return new User(this);
			}
		}
	}
}
