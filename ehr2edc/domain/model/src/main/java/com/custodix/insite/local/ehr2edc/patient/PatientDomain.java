package com.custodix.insite.local.ehr2edc.patient;

import java.util.Objects;

public final class PatientDomain {
	private final String name;

	private PatientDomain(Builder builder) {
		name = builder.name;
	}

	public static PatientDomain of(String name) {
		return PatientDomain.newBuilder()
				.withName(name)
				.build();
	}

	public String getName() {
		return name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final PatientDomain that = (PatientDomain) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public static final class Builder {
		private String name;

		private Builder() {
		}

		public Builder withName(String val) {
			name = val;
			return this;
		}

		public PatientDomain build() {
			return new PatientDomain(this);
		}
	}
}