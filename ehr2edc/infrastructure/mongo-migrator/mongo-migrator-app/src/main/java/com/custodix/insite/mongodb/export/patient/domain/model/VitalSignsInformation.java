package com.custodix.insite.mongodb.export.patient.domain.model;

import java.util.Optional;

public class VitalSignsInformation {
	private final String locationCDISCCode;
	private final String locationCode;
	private final String lateralityCDISCCode;
	private final String lateralityCode;
	private final String positionCDISCode;
	private final String positionCode;

	private VitalSignsInformation(Builder builder) {
		locationCDISCCode = builder.locationCDISCCode;
		locationCode = builder.locationCode;
		lateralityCDISCCode = builder.lateralityCDISCCode;
		lateralityCode = builder.lateralityCode;
		positionCDISCode = builder.positionCDISCCode;
		positionCode = builder.positionCode;
	}

	public Optional<String> getLocationCDISCCode() {
		return Optional.ofNullable(locationCDISCCode)
				.filter(s -> !s.isEmpty());
	}

	public Optional<String> getLocationCode() {
		return Optional.ofNullable(locationCode)
				.filter(s -> !s.isEmpty());
	}

	public Optional<String> getLateralityCDISCCode() {
		return Optional.ofNullable(lateralityCDISCCode)
				.filter(s -> !s.isEmpty());
	}

	public Optional<String> getLateralityCode() {
		return Optional.ofNullable(lateralityCode)
				.filter(s -> !s.isEmpty());
	}

	public Optional<String> getPositionCDISCode() {
		return Optional.ofNullable(positionCDISCode)
				.filter(s -> !s.isEmpty());
	}

	public Optional<String> getPositionCode() {
		return Optional.ofNullable(positionCode)
				.filter(s -> !s.isEmpty());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String locationCDISCCode;
		private String locationCode;
		private String lateralityCDISCCode;
		private String lateralityCode;
		private String positionCDISCCode;
		private String positionCode;

		private Builder() {
		}

		public Builder withLocationCDISCCode(String val) {
			locationCDISCCode = val;
			return this;
		}

		public Builder withLocationName(String val) {
			locationCode = val;
			return this;
		}

		public Builder withLateralityCDISCCode(String val) {
			lateralityCDISCCode = val;
			return this;
		}

		public Builder withLateralityName(String val) {
			lateralityCode = val;
			return this;
		}

		public Builder withPositionCDISCCode(String val) {
			positionCDISCCode = val;
			return this;
		}

		public Builder withPositionName(String val) {
			positionCode = val;
			return this;
		}

		public VitalSignsInformation build() {
			return new VitalSignsInformation(this);
		}
	}
}