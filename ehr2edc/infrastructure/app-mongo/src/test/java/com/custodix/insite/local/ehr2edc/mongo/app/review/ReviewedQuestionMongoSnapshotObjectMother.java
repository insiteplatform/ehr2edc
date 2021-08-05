package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static java.util.Collections.singletonMap;

public class ReviewedQuestionMongoSnapshotObjectMother {

	public static ReviewedQuestionMongoSnapshot aDefaultReviewedQuestionMongoSnapshot() {
		return ReviewedQuestionMongoSnapshot.newBuilder()
				.withTranslations(singletonMap("key", "translation"))
				.build();
	}
}