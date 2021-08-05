package com.custodix.insite.local.ehr2edc.snapshots;

public final class MeasurementUnitSnapshot {
	private final String id;
	private final String name;

	private MeasurementUnitSnapshot(final Builder builder) {
		id = builder.id;
		name = builder.name;
	}

	public static Builder newBuilder() {
		return new Builder();
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

		public MeasurementUnitSnapshot build() {
			return new MeasurementUnitSnapshot(this);
		}
	}
}
