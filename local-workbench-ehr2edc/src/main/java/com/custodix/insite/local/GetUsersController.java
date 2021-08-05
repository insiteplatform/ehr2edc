package com.custodix.insite.local;

import java.util.Collection;

public interface GetUsersController {

	Response getUsers();

	final class Response {
		private final Collection<User> users;

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

		public Collection<User> getUsers() {
			return users;
		}

		public static final class Builder {
			private Collection<User> users;

			private Builder() {
			}

			public Builder withUsers(Collection<User> users) {
				this.users = users;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class User {
		private final String name;
		private final Long id;
		private final boolean drm;

		private User(Builder builder) {
			name = builder.name;
			id = builder.id;
			drm = builder.drm;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(User copy) {
			Builder builder = new Builder();
			builder.name = copy.getName();
			builder.id = copy.getId();
			return builder;
		}

		public String getName() {
			return name;
		}

		public Long getId() {
			return id;
		}

		public boolean isDrm() {
			return drm;
		}

		public static final class Builder {
			private String name;
			private Long id;
			private boolean drm;

			private Builder() {
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withId(Long id) {
				this.id = id;
				return this;
			}

			public Builder withDrm(boolean drm) {
				this.drm = drm;
				return this;
			}

			public User build() {
				return new User(this);
			}
		}
	}
}
