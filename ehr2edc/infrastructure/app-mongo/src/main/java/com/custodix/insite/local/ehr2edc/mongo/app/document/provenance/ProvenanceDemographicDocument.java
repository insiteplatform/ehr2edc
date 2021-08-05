package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.DemographicType;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;

@TypeAlias("ProvenanceDemographic")
public final class ProvenanceDemographicDocument implements ProvenanceDataPointDocument {
	private final String value;
	private final DemographicType demographicType;

	@PersistenceConstructor
	private ProvenanceDemographicDocument(String value, DemographicType demographicType) {
		this.value = value;
		this.demographicType = demographicType;
	}

	private ProvenanceDemographicDocument(Builder builder) {
		value = builder.value;
		demographicType = builder.demographicType;
	}

	public static ProvenanceDemographicDocument toDocument(ProvenanceDemographic provenanceDemographic) {
		return ProvenanceDemographicDocument.newBuilder()
				.withValue(provenanceDemographic.getValue())
				.withDemographicType(provenanceDemographic.getDemographicType())
				.build();
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

	private static final class Builder {
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

		public ProvenanceDemographicDocument build() {
			return new ProvenanceDemographicDocument(this);
		}
	}
}
