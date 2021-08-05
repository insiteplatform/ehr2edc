package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

public final class SubmittedItemId {
	@NotBlank
	private final String id;

	private SubmittedItemId(Builder builder) {
		id = builder.id;
	}

	public static SubmittedItemId of(String id) {
		return SubmittedItemId.newBuilder().withId(id).build();
	}

	public static SubmittedItemId newId() {
		return of(UUID.randomUUID().toString());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SubmittedItemId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SubmittedItemId that = (SubmittedItemId) o;
		return Objects.equals(id, that.id);
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

		public SubmittedItemId build() {
			return new SubmittedItemId(this);
		}
	}
}
