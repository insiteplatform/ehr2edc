package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedItemLabelMongoSnapshotObjectMother.aDefaultReviewedItemLabelMongoSnapshotBuilder;
import static com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDemographicDocumentObjectMother.aDefaultReviewedProvenanceDemographicDocument;

public class ReviewedItemMongoSnapshotObjectMother {

	public static ReviewedItemMongoSnapshot.Builder aDefaultReviewedItemMongoSnapshotBuilder() {
		return ReviewedItemMongoSnapshot.newBuilder()
				.withInstanceId("itemInstanceId")
				.withId("itemId")
				.withLabel(aDefaultReviewedItemLabelMongoSnapshotBuilder().build())
				.withDataPoint(aDefaultReviewedProvenanceDemographicDocument())
				.withLabeledValue(ReviewedLabeledValueDocumentObjectMother.aDefaultReviewedLabeledValueDocument())
				.withKey(true)
				.withPopulatedItemId("populatedItemId");
	}
}