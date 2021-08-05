package com.custodix.insite.local.ehr2edc.mongo.app.review;

import java.util.Collections;

public class ReviewedFormMongoSnapshotObjectMother {

	public static ReviewedFormMongoSnapshot.Builder aDefaultReviewedFormMongoSnapshotBuilder() {
		return ReviewedFormMongoSnapshot.newBuilder()
				.withName("reviewedForm.name")
				.withFormDefinitionId("formDefinitionId")
				.withReviewedItemGroups(Collections.singletonList(
						ReviewedItemGroupMongoSnapshotObjectMother.aDefaultReviewedItemGroupMongoSnapshotBuilder().build()))
				.withPopulatedFormId("populatedFormId");
	}
}