package com.custodix.insite.local.ehr2edc.submitted;

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedLabelObjectMother.aDefaultSubmittedLabel;

import java.util.Collections;

public class SubmittedLabeledValueObjectMother {

	public static SubmittedLabeledValue aDefaultSubmittedLabeledValue() {
		return aDefaultSubmittedLabeledValueBuilder().build();
	}

	public static SubmittedLabeledValue.Builder aDefaultSubmittedLabeledValueBuilder() {
		return SubmittedLabeledValue.newBuilder()
				.withLabels(Collections.singletonList(aDefaultSubmittedLabel()))
				.withValue("itemValue");
	}
}
