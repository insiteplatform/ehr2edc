package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.DemographicType;

public class ReviewedProvenanceDemographicDocumentObjectMother {

	public static ReviewedProvenanceDemographicDocument aDefaultReviewedProvenanceDemographicDocument() {
		return aDefaultReviewedProvenanceDemographicDocumentBuilder()
				.build();
	}

	private static ReviewedProvenanceDemographicDocument.Builder aDefaultReviewedProvenanceDemographicDocumentBuilder() {
		return ReviewedProvenanceDemographicDocument.newBuilder()
				.withDemographicType(DemographicType.GENDER)
				.withValue("M");
	}
}
