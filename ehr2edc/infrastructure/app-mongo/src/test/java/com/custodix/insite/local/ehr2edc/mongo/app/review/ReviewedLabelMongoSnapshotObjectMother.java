package com.custodix.insite.local.ehr2edc.mongo.app.review;

import java.util.Locale;

public class ReviewedLabelMongoSnapshotObjectMother {

	public static ReviewedLabelDocument aDefaultReviewedLabelMongoSnapshot() {
		return aDefaultReviewedLabelMongoSnapshotBuilder().build();
	}

	public static ReviewedLabelDocument.Builder aDefaultReviewedLabelMongoSnapshotBuilder() {
		return ReviewedLabelDocument.newBuilder()
				.withText("text-label-123")
				.withLocale(Locale.ENGLISH);
	}
}
