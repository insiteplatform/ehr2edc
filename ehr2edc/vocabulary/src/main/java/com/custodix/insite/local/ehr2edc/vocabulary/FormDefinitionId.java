package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class FormDefinitionId {

	@NotNull
	@NotBlank
	private final String id;

	private FormDefinitionId(Builder builder) {
		id = builder.id;
	}

	public static FormDefinitionId of(String id) {
		return FormDefinitionId.newBuilder()
				.withId(id)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final FormDefinitionId formDefinitionId = (FormDefinitionId) o;
		return Objects.equals(id, formDefinitionId.id);
	}

	@Override
	public String toString() {
		return "FormDefinitionId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(FormDefinitionId copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public FormDefinitionId build() {
			return new FormDefinitionId(this);
		}
	}
}
