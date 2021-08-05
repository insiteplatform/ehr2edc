package com.custodix.insite.mongodb.export.patient.domain.model.common;

import java.util.List;

public class Observation {
	private final ObservationType type;
	private final List<ObservationValue<?>> values;

	private Observation(final Builder builder) {
		type = builder.type;
		values = builder.values;
	}

	public static Builder newBuilder(final Observation copy) {
		Builder builder = new Builder();
		builder.type = copy.getType();
		builder.values = copy.getValues();
		return builder;
	}

	public ObservationType getType() {
		return type;
	}

	public List<ObservationValue<?>> getValues() {
		return values;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private List<ObservationValue<?>> values;
		private ObservationType type;

		private Builder() {
		}

		public Builder withValues(List<ObservationValue<?>> val) {
			values = val;
			return this;
		}

		public Observation build() {
			return new Observation(this);
		}

		public Builder withType(final ObservationType val) {
			type = val;
			return this;
		}
	}
}