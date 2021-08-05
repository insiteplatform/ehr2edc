package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public final class PatientCDWReference {

	public static final int PATIENT_ID_SOURCE_MAX_LENGTH = 50;
	private static final int PATIENT_ID_IDENTIFIER_MAX_LENGTH = 200;

	@NotNull
	@Size(min = 1,
		  max = PATIENT_ID_SOURCE_MAX_LENGTH)
	private final String source;

	@NotNull
	@Size(min = 1,
		  max = PATIENT_ID_IDENTIFIER_MAX_LENGTH)
	private final String id;

	private PatientCDWReference(String source, String id) {
		this.source = source;
		this.id = id;
	}

	private static PatientCDWReference fromBuilder(Builder builder) {
		return new PatientCDWReference(builder.source, builder.id);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(PatientCDWReference copy) {
		Builder builder = new Builder();
		builder.source = copy.getSource();
		builder.id = copy.getId();
		return builder;
	}

	public String getSource() {
		return source;
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
		final PatientCDWReference patientCDWReference = (PatientCDWReference) o;
		return Objects.equals(source, patientCDWReference.source) && Objects.equals(id, patientCDWReference.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, id);
	}

	public static final class Builder {
		private String source;
		private String id;

		private Builder() {
		}

		public Builder withSource(String val) {
			source = val;
			return this;
		}

		public Builder withId(String val) {
			id = val;
			return this;
		}

		public PatientCDWReference build() {
			return PatientCDWReference.fromBuilder(this);
		}
	}
}
