package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.snapshots.MeasurementUnitSnapshot;

public final class MeasurementUnit {
	private String id;
	private String name;

	private MeasurementUnit(final Builder builder) {
		id = builder.id;
		name = builder.name;
	}

	private MeasurementUnit(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static List<MeasurementUnit> restoreFrom(List<MeasurementUnitSnapshot> measurementUnits) {
		return measurementUnits == null ?
				new ArrayList<>() :
				measurementUnits.stream()
						.map(MeasurementUnit::restoreFrom)
						.collect(Collectors.toList());
	}

	public MeasurementUnitSnapshot toSnapshot() {
		return MeasurementUnitSnapshot.newBuilder()
				.withId(id)
				.withName(name)
				.build();
	}

	private static MeasurementUnit restoreFrom(MeasurementUnitSnapshot measurementUnit) {
		return new MeasurementUnit(measurementUnit.getId(), measurementUnit.getName());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static final class Builder {
		private String id;
		private String name;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public MeasurementUnit build() {
			return new MeasurementUnit(this);
		}
	}
}
