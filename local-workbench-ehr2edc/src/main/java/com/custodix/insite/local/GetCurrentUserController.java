package com.custodix.insite.local;

public interface GetCurrentUserController {

	Response get();

	final class Response {
		private final User user;

		private Response(Builder builder) {
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
