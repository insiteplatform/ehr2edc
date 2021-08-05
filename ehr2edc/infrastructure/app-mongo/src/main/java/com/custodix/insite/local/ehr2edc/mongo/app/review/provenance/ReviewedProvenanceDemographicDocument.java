package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.DemographicType;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;

@TypeAlias("ReviewedProvenanceDemographic")
final class ReviewedProvenanceDemographicDocument implements ReviewedProvenanceDataPointDocument {
	private final String value;
	private final DemographicType demographicType;

	@PersistenceConstructor
	private ReviewedProvenanceDemographicDocument(String value, DemographicType demographicType) {
		this.value = value;
		this.demographicType = demographicType;
	}

	private ReviewedProvenanceDemographicDocument(Builder builder) {
		value = builder.value;
		demographicType = builder.demographicType;
	}

	@Override
	public ProvenanceDataPoint restore() {
		return ProvenanceDemographic.newBuilder()
				.withValue(value)
				.withDemographicType(demographicType)
				.build();
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

		public ReviewedProvenanceDemographicDocument build() {
			return new ReviewedProvenanceDemographicDocument(this);
		}
	}
}
