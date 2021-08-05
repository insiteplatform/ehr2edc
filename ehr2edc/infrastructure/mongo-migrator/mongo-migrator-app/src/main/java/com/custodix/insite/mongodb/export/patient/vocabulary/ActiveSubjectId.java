package com.custodix.insite.mongodb.export.patient.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

public final class ActiveSubjectId {
	private final String id;

	private ActiveSubjectId(String id) {
		this.id = id;
	}

	private ActiveSubjectId(Builder builder) {
		id = builder.id;
	}

	public static ActiveSubjectId generate() {
		final UUID uuid = UUID.randomUUID();
		final Long generatedId = abs(uuid.getMostSignificantBits());
		return new ActiveSubjectId(abs(generatedId.intValue()) + "");
	}
	public static ActiveSubjectId of(String id) {
		return ActiveSubjectId.newBuilder()
				.withId(id)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
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
		final ActiveSubjectId subjectId = (ActiveSubjectId) o;
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

		public ActiveSubjectId build() {
			return new ActiveSubjectId(this);
		}
	}

}
