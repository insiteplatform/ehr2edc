package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedQuestionMongoSnapshotObjectMother.aDefaultReviewedQuestionMongoSnapshot;

public class ReviewedItemLabelMongoSnapshotObjectMother {

	public static ReviewedItemLabelMongoSnapshot.Builder aDefaultReviewedItemLabelMongoSnapshotBuilder() {
		return ReviewedItemLabelMongoSnapshot.newBuilder()
				.withQuestion(aDefaultReviewedQuestionMongoSnapshot())
				.withName("labelName");
	}
}