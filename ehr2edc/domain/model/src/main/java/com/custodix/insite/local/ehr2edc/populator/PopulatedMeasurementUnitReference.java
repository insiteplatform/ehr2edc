package com.custodix.insite.local.ehr2edc.populator;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedMeasurementUnitReference;

public final class PopulatedMeasurementUnitReference {
	private final String id;
	private final boolean readOnly;

	private PopulatedMeasurementUnitReference(Builder builder) {
		id = builder.id;
		readOnly = builder.readOnly;
	}

	SubmittedMeasurementUnitReference toReviewed() {
		return SubmittedMeasurementUnitReference.newBuilder()
				.withId(id)
				.withSubmittedToEDC(!readOnly)
				.build();
	}

	public String getId() {
		return id;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private boolean readOnly;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}

		public PopulatedMeasurementUnitReference build() {
			return new PopulatedMeasurementUnitReference(this);
		}
	}
}
