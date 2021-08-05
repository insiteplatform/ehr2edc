package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotEmpty;

public final class FormId {
	@NotEmpty
	private final String id;

	private FormId(Builder builder) {
		id = builder.id;
	}

	public static FormId of(String value) {
		return FormId.newBuilder().withId(value).build();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FormId{" + "id='" + id + '\'' + '}';
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

		public FormId build() {
			return new FormId(this);
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
		final FormId formId = (FormId) o;
		return id.equals(formId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}