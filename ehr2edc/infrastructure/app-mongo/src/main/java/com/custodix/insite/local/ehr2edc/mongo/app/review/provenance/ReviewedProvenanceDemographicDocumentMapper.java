package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ReviewedProvenanceDemographicDocumentMapper implements ReviewedProvenanceDataPointMapper {

	@Override
	public boolean supports(final ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceDemographic;
	}

	@Override
	public ReviewedProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return toDocument((ProvenanceDemographic) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}

	private ReviewedProvenanceDemographicDocument toDocument(ProvenanceDemographic provenanceDemographic) {
		return ReviewedProvenanceDemographicDocument.newBuilder()
				.withValue(provenanceDemographic.getValue())
				.withDemographicType(provenanceDemographic.getDemographicType())
				.build();
	}
}
