package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;

public final class ProvenanceDemographic implements ProvenanceDataPoint {
	private final String value;
	private final DemographicType demographicType;

	private ProvenanceDemographic(Builder builder) {
		value = builder.value;
		demographicType = builder.demographicType;
	}

	public static ProvenanceDemographic from(Demographic demographic) {
		DemographicType demographicType = Optional.ofNullable(demographic.getDemographicType())
				.map(DemographicType::from)
				.orElse(null);
		return ProvenanceDemographic.newBuilder()
				.withValue(demographic.getValue())
				.withDemographicType(demographicType)
				.build();
	}

	public String getValue() {
		return value;
	}

	public DemographicType getDemographicType() {
		return demographicType;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String value;
		private DemographicType demographicType;

		private Builder() {
		}

		public Builder withValue(String value) {
			this.value = value;
			return this;
		}

		public Builder withDemographicType(DemographicType demographicType) {
			this.demographicType = demographicType;
			return this;
		}

		public ProvenanceDemographic build() {
			return new ProvenanceDemographic(this);
		}
	}
}
