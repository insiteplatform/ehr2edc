package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class SubmissionContextId {
	@NotBlank
	private final String id;

	private SubmissionContextId(Builder builder) {
		id = builder.id;
	}

	public static SubmissionContextId of(String value) {
		return SubmissionContextId.newBuilder().withId(value).build();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SubmissionContextId{" + "id='" + id + '\'' + '}';
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

		public SubmissionContextId build() {
			return new SubmissionContextId(this);
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
		final SubmissionContextId formId = (SubmissionContextId) o;
		return id.equals(formId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}