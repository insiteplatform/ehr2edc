package com.custodix.insite.local.ehr2edc.query.security;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface GetUser {
	@Allow(ANYONE)
	Response getUser(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final UserIdentifier userIdentifier;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
		}

		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final User user;

		private Response(Builder builder) {
			user = builder.user;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.user = copy.getUser();
			return builder;
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
		private final UserIdentifier userIdentifier;
		private final String name;
		private final boolean drm;

		private User(Builder builder) {
			userIdentifier = builder.userIdentifier;
			name = builder.name;
			drm = builder.drm;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public String getName() {
			return name;
		}

		public boolean isDrm() {
			return drm;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private String name;
			private boolean drm;

			private Builder() {
			}

			public Builder withUserId(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
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
