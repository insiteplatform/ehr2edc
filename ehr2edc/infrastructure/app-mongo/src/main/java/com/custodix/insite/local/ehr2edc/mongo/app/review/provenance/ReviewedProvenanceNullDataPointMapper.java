package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;

public final class ReviewedProvenanceNullDataPointMapper implements ReviewedProvenanceDataPointMapper {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint == null;
	}

	@Override
	public ReviewedProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		return null;
	}
}
