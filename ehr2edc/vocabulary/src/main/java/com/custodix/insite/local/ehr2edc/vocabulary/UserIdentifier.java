package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public final class UserIdentifier {
	@NotNull
	private final String id;

	private UserIdentifier(Builder builder) {
		id = builder.id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(UserIdentifier copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	public static UserIdentifier of(String id) {
		return UserIdentifier.newBuilder()
				.withId(id)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "UserIdentifier{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final UserIdentifier userIdentifier = (UserIdentifier) o;
		return Objects.equals(id, userIdentifier.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public UserIdentifier build() {
			return new UserIdentifier(this);
		}
	}
}