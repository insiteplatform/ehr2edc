package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public final class StudyId {

	private static final int STUDY_ID_MAX_LENGTH = 50;

	@NotBlank
	@Size(min = 1,
		  max = STUDY_ID_MAX_LENGTH)
	private final String id;

	private StudyId(Builder builder) {
		id = builder.id;
	}

	public String getId() {
		return id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(StudyId copy) {
		Builder builder = new Builder();
		builder.id = copy.id;
		return builder;
	}

	public static StudyId of(String id) {
		return StudyId.newBuilder()
				.withId(id)
				.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final StudyId studyId = (StudyId) o;
		return Objects.equals(id, studyId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "StudyId{" + "id='" + id + '\'' + '}';
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public StudyId build() {
			return new StudyId(this);
		}
	}
}
