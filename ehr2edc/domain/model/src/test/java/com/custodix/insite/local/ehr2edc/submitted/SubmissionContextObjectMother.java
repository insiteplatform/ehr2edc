package com.custodix.insite.local.ehr2edc.submitted;

import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;

public final class SubmissionContextObjectMother {


	public static SubmissionContext aDefaultSubmissionContext() {
		return aDefaultSubmissionContextBuilder().build();
	}

	public static SubmissionContext.Builder aDefaultSubmissionContextBuilder() {
		return SubmissionContext.newBuilder()
				.withSubmittedEventId(SubmittedEventId.of("event-id-098"))
				.withSubmittedXml("<submittedXml />");
	}
}