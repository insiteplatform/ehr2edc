package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public final class LabName {
	private static final int LAB_NAME_MAX_LENGTH = 50;

	@NotBlank
	@Size(max = LAB_NAME_MAX_LENGTH)
	private final String name;

	private LabName(Builder builder) {
		name = builder.name;
	}

	public String getName() {
		return name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(LabName copy) {
		Builder builder = new Builder();
		builder.name = copy.name;
		return builder;
	}

	public static LabName of(String name) {
		return newBuilder()
				.withName(name)
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
		final LabName labName = (LabName) o;
		return Objects.equals(name, labName.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "Lab{" + "name='" + name + "'" + "}";
	}

	public static final class Builder {
		private String name;

		private Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public LabName build() {
			return new LabName(this);
		}
	}
}
