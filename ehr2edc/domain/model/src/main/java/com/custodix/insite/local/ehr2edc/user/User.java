package com.custodix.insite.local.ehr2edc.user;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public final class User {
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
