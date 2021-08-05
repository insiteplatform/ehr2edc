package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public final class InvestigatorSnapshot {
	private final UserIdentifier userId;
	private final String name;

	private InvestigatorSnapshot(Builder builder) {
		userId = builder.userId;
		name = builder.name;
	}

	public UserIdentifier getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private UserIdentifier userId;
		private String name;

		private Builder() {
		}

		public Builder withUserId(UserIdentifier userId) {
			this.userId = userId;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public InvestigatorSnapshot build() {
			return new InvestigatorSnapshot(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final InvestigatorSnapshot that = (InvestigatorSnapshot) o;
		return Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}
}
