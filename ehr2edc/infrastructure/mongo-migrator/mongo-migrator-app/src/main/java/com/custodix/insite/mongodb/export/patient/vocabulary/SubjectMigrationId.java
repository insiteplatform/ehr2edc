package com.custodix.insite.mongodb.export.patient.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

public final class SubjectMigrationId {
	private final String id;

	private SubjectMigrationId(String id) {
		this.id = id;
	}

	private SubjectMigrationId(Builder builder) {
		id = builder.id;
	}

	public static SubjectMigrationId generate() {
		final UUID uuid = UUID.randomUUID();
		final Long generatedId = abs(uuid.getMostSignificantBits());
		return new SubjectMigrationId(abs(generatedId.intValue()) + "");
	}
	public static SubjectMigrationId of(String id) {
		return SubjectMigrationId.newBuilder()
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
		final SubjectMigrationId subjectId = (SubjectMigrationId) o;
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

		public SubjectMigrationId build() {
			return new SubjectMigrationId(this);
		}
	}

}
