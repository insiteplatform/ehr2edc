package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedItemMongoSnapshotObjectMother.aDefaultReviewedItemMongoSnapshotBuilder;

import java.util.Collections;

public class ReviewedItemGroupMongoSnapshotObjectMother {

	public static ReviewedItemGroupMongoSnapshot.Builder aDefaultReviewedItemGroupMongoSnapshotBuilder() {
		return ReviewedItemGroupMongoSnapshot.newBuilder()
				.withId("itemGroupId")
				.withRepeating(true)
				.withReviewedItems(Collections.singletonList(aDefaultReviewedItemMongoSnapshotBuilder().build()))
				.withPopulatedItemGroupId("populatedItemGroupId");
	}
}