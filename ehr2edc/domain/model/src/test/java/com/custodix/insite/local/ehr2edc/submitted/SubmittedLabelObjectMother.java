package com.custodix.insite.local.ehr2edc.submitted;

import java.util.Locale;

public class SubmittedLabelObjectMother {

	public static SubmittedLabel aDefaultSubmittedLabel() {
		return aDefaultSubmittedLabelBuilder().build();
	}

	public static SubmittedLabel.Builder aDefaultSubmittedLabelBuilder() {
		return SubmittedLabel.newBuilder()
				.withText("text-label-123")
				.withLocale(Locale.ENGLISH);
	}
}
