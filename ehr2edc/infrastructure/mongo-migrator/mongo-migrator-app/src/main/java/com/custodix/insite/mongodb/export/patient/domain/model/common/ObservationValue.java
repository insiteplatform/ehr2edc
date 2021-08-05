package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class ObservationValue<T> {
	private final T value;
	private final String unit;

	private ObservationValue(Builder<T> builder) {
		value = builder.value;
		unit = builder.unit;
	}

	public T getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public static <T> Builder<T> newBuilder() {
		return new Builder<>();
	}

	public static final class Builder<T> {
		private T value;
		private String unit;

		private Builder() {
		}

		public Builder withValue(T val) {
			value = val;
			return this;
		}

		public Builder withUnit(String val) {
			unit = val;
			return this;
		}

		public ObservationValue build() {
			return new ObservationValue<>(this);
		}
	}
}