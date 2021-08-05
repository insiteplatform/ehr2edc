package com.custodix.insite.local.ehr2edc.mongo.app.audit;

import java.time.Instant;
import java.util.Collections;

import com.custodix.insite.local.ehr2edc.mongo.app.document.FormMongoSnapshotObjectMother;
import com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedEventMongoSnapshotObjectMother;

public class ReviewContextDocumentObjectMother {

	public static ReviewContextDocument.Builder aDefaultReviewContextDocumentBuilder() {
		return ReviewContextDocument.newBuilder()
				.withId("SubmittedEventId")
				.withPopulatedForms(Collections.singletonList(FormMongoSnapshotObjectMother.aDefaultFormMongoSnapshotBuilder().build()))
				.withReviewerUserId("UserId")
				.withReviewDate(Instant.now())
				.withSubmittedXml("<submittedXML></submittedXML>")
				.withReviewedEvent(ReviewedEventMongoSnapshotObjectMother.aDefaultReviewedEventMongoSnapshotBuilder().build());
	}
}