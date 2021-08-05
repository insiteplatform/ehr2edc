package com.custodix.insite.local.ehr2edc.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public final class SubjectId {

	private static final int SUBJECT_ID_MAX_LENGTH = 200;

	@NotBlank
	@Size(min = 1,
		  max = SUBJECT_ID_MAX_LENGTH)
	private final String id;

	private SubjectId(String id) {
		this.id = id;
	}

	private static SubjectId fromBuilder(Builder builder) {
		return new SubjectId(builder.id);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(SubjectId copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	public static SubjectId generate() {
		final UUID uuid = UUID.randomUUID();
		final Long generatedId = abs(uuid.getMostSignificantBits());
		return new SubjectId(abs(generatedId.intValue()) + "");
	}

	public static SubjectId of(String id) {
		return SubjectId.newBuilder()
				.withId(id)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SubjectId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubjectId subjectId = (SubjectId) o;
		return Objects.equals(id, subjectId.id);
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

		public SubjectId build() {
			return SubjectId.fromBuilder(this);
		}
	}
}
