package com.custodix.insite.local.ehr2edc.submitted;

public final class SubmittedMeasurementUnitReference {
	private final String id;
	private final boolean submittedToEDC;

	private SubmittedMeasurementUnitReference(Builder builder) {
		id = builder.id;
		submittedToEDC = builder.submittedToEDC;
	}

	public boolean isSubmittedToEDC() {
		return submittedToEDC;
	}

	public String getId() {
		return id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private boolean submittedToEDC;

		private Builder() {
		}

		public Builder withId(String val) {
			id = val;
			return this;
		}

		public Builder withSubmittedToEDC(boolean val) {
			submittedToEDC = val;
			return this;
		}

		public SubmittedMeasurementUnitReference build() {
			return new SubmittedMeasurementUnitReference(this);
		}
	}
}
