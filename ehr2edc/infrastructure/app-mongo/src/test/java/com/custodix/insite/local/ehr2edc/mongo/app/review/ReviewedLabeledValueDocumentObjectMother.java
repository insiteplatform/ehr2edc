package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedLabelMongoSnapshotObjectMother.aDefaultReviewedLabelMongoSnapshot;

import java.util.Collections;

public class ReviewedLabeledValueDocumentObjectMother {

	public static ReviewedLabeledValueDocument aDefaultReviewedLabeledValueDocument() {
		return aDefaultReviewedLabeledValueDocumentBuilder().build();
	}

	public static ReviewedLabeledValueDocument.Builder aDefaultReviewedLabeledValueDocumentBuilder() {
		return ReviewedLabeledValueDocument.newBuilder()
				.withLabels(Collections.singletonList(aDefaultReviewedLabelMongoSnapshot()))
				.withValue("itemValue");
	}
}
