package com.custodix.insite.mongodb.export.patient.domain.model.labvalue;

import java.util.Optional;
import java.util.function.Predicate;

import com.custodix.insite.mongodb.export.patient.domain.model.FastingStatus;

public class LaboratoryConceptInfo {

	private static final Predicate<String> NOT_EMPTY = s -> !s.isEmpty();

	private final String specimenValue;
	private final String specimenText;
	private final String methodValue;
	private final String methodText;
	private final FastingStatus fastingStatus;
	private final String component;

	private LaboratoryConceptInfo(Builder builder) {
		specimenValue = builder.specimenValue;
		specimenText = builder.specimenText;
		methodValue = builder.methodValue;
		methodText = builder.methodText;
		fastingStatus = builder.fastingStatus;
		component = builder.component;
	}

	public String getSpecimenValue() {
		return specimenValue;
	}

	public String getSpecimenText() {
		return specimenText;
	}

	public Optional<String> getMethodValue() {
		return Optional.ofNullable(methodValue)
				.filter(NOT_EMPTY);
	}

	public Optional<String> getMethodText() {
		return Optional.ofNullable(methodText)
				.filter(NOT_EMPTY);
	}

	public FastingStatus getFastingStatus() {
		return fastingStatus;
	}

	public String getComponent() {
		return component;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String specimenValue;
		private String specimenText;
		private String methodValue;
		private String methodText;
		private FastingStatus fastingStatus;
		private String component;

		private Builder() {
		}

		public Builder withSpecimenValue(String val) {
			specimenValue = val;
			return this;
		}

		public Builder withSpecimenText(String val) {
			specimenText = val;
			return this;
		}

		public Builder withMethodValue(String val) {
			methodValue = val;
			return this;
		}

		public Builder withMethodText(String val) {
			methodText = val;
			return this;
		}

		public Builder withFastingStatus(FastingStatus val) {
			fastingStatus = val;
			return this;
		}

		public Builder withComponent(String val) {
			component = val;
			return this;
		}

		public LaboratoryConceptInfo build() {
			return new LaboratoryConceptInfo(this);
		}
	}
}