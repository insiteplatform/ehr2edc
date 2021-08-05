package com.custodix.insite.local.ehr2edc.mongo.app.document;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference;

public final class MeasurementUnitReferenceDocument {
	private final String id;

	@PersistenceConstructor
	private MeasurementUnitReferenceDocument(String id) {
		this.id = id;
	}

	private MeasurementUnitReferenceDocument(Builder builder) {
		id = builder.id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	static MeasurementUnitReferenceDocument restoreFrom(PopulatedMeasurementUnitReference measurementUnitReference) {
		return MeasurementUnitReferenceDocument.newBuilder()
				.withId(measurementUnitReference.getId())
				.build();
	}

	PopulatedMeasurementUnitReference toMeasurementUnitReference(boolean outputUnit) {
		return PopulatedMeasurementUnitReference.newBuilder()
				.withId(id)
				.withReadOnly(!outputUnit)
				.build();
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public MeasurementUnitReferenceDocument build() {
			return new MeasurementUnitReferenceDocument(this);
		}
	}
}
