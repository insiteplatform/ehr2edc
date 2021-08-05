package com.custodix.insite.local.ehr2edc.provenance.model;

public class ProvenanceDemographicObjectMother {
	public static final String MALE_VALUE = "M";

	public static ProvenanceDemographic aDefaultProvenanceDemographic() {
		return aDefaultProvenanceDemographicBuilder().build();
	}

	public static ProvenanceDemographic.Builder aDefaultProvenanceDemographicBuilder() {
		return ProvenanceDemographic.newBuilder()
				.withDemographicType(DemographicType.GENDER)
				.withValue("M");
	}

	public static ProvenanceDemographic male() {
		return ProvenanceDemographic.newBuilder()
				.withDemographicType(DemographicType.GENDER)
				.withValue(MALE_VALUE)
				.build();
	}

	public static ProvenanceDemographic empty() {
		return ProvenanceDemographic.newBuilder()
				.build();
	}
}