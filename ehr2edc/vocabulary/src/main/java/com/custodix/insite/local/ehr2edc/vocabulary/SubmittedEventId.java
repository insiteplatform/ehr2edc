package com.custodix.insite.local.ehr2edc.vocabulary;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

public final class SubmittedEventId {
	@NotBlank
	private final String id;

	private SubmittedEventId(Builder builder) {
		id = builder.id;
	}

	public static SubmittedEventId of(String value) {
		return SubmittedEventId.newBuilder().withId(value).build();
	}

	public static SubmittedEventId newId() {
		return of(UUID.randomUUID().toString());
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SubmittedEventId{" + "id='" + id + '\'' + '}';
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String val) {
			id = val;
			return this;
		}

		public SubmittedEventId build() {
			return new SubmittedEventId(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubmittedEventId formId = (SubmittedEventId) o;
		return id.equals(formId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}