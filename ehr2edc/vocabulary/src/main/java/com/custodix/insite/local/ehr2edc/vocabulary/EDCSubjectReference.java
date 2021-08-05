package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class EDCSubjectReference {

	private static final int ID_MAX_LENGTH = 200;

	@NotNull
	@NotBlank
	@Size(min = 1,
		  max = ID_MAX_LENGTH)
	private final String id;

	private EDCSubjectReference(String id) {
		this.id = id;
	}

	private static EDCSubjectReference fromBuilder(Builder builder) {
		return new EDCSubjectReference(builder.id);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(EDCSubjectReference copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	public static EDCSubjectReference of(String id) {
		return EDCSubjectReference.newBuilder()
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
		final EDCSubjectReference subjectId = (EDCSubjectReference) o;
		return Objects.equals(id, subjectId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "EDCSubjectReference{" + "id='" + id + '\'' + '}';
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public EDCSubjectReference build() {
			return EDCSubjectReference.fromBuilder(this);
		}
	}
}
