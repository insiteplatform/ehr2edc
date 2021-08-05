package com.custodix.insite.mongodb.export.patient.domain.model.demographic;

public class VitalStatus implements DemographicFact {

	private static final String DEMOGRAPHIC_TYPE = "VITAL_STATUS";

	private final Status status;

	private VitalStatus(Builder builder) {
		status = builder.status;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public String factType() {
		return DEMOGRAPHIC_TYPE;
	}

	@Override
	public String factValue() {
		return String.valueOf(status);
	}

	public static final class Builder {
		private Status status;

		private Builder() {
		}

		public Builder withStatus(Status status) {
			this.status = status;
			return this;
		}

		public VitalStatus build() {
			return new VitalStatus(this);
		}
	}

	public enum Status {
		ALIVE,
		UNKNOWN,
		DECEASED
	}
}
