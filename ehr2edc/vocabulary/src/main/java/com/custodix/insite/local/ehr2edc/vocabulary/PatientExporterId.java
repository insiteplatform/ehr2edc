package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

public final class PatientExporterId {

	private final String id;

	private PatientExporterId(Builder builder) {
		id = builder.id;
	}

	public static PatientExporterId of(String value) {
		return PatientExporterId.newBuilder()
				.withId(value)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "PatientExporterId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final PatientExporterId that = (PatientExporterId) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static Builder newBuilder() {
		return new Builder();
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
