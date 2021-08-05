package com.custodix.insite.local.ehr2edc.mongo.app.review;

import java.util.Collections;

public class ReviewedEventMongoSnapshotObjectMother {

	public static ReviewedEventDocument.Builder aDefaultReviewedEventMongoSnapshotBuilder() {
		return ReviewedEventDocument.newBuilder()
				.withEventDefinitionId("eventDefinitionId")
				.withEventParentId("eventParentId")
				.withStudyId("studyId")
				.withSubjectId("subjectId")
				.withReviewedForms(Collections.singletonList(ReviewedFormMongoSnapshotObjectMother.aDefaultReviewedFormMongoSnapshotBuilder().build()))
				.withPopulatedEventId("populatedEventId");
	}
}