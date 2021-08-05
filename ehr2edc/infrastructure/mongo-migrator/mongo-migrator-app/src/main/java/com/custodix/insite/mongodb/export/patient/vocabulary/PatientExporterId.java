package com.custodix.insite.mongodb.export.patient.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

public final class PatientExporterId {
	private final String id;

	private PatientExporterId(String id) {
		this.id = id;
	}

	private PatientExporterId(Builder builder) {
		id = builder.id;
	}

	public static PatientExporterId generate() {
		final UUID uuid = UUID.randomUUID();
		final Long generatedId = abs(uuid.getMostSignificantBits());
		return new PatientExporterId(String.valueOf(abs(generatedId.intValue())));
	}
	public static PatientExporterId of(String id) {
		return PatientExporterId.newBuilder()
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
		final PatientExporterId subjectId = (PatientExporterId) o;
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

		public PatientExporterId build() {
			return new PatientExporterId(this);
		}
	}

}
