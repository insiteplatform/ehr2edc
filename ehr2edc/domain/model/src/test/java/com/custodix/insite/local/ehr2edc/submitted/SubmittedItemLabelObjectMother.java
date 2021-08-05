package com.custodix.insite.local.ehr2edc.submitted;

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedQuestionObjectMother.aDefaultSubmittedQuestion;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.snapshots.ItemLabelSnapshot;

public class SubmittedItemLabelObjectMother {
	public static SubmittedItemLabel.Builder aDefaultSubmittedItemLabelBuilder() {
		return SubmittedItemLabel.newBuilder()
				.withQuestion(aDefaultSubmittedQuestion())
				.withName("labelName");
	}

	public static SubmittedItemLabel aSubmittedItemLabel(ItemLabel itemLabel) {
		ItemLabelSnapshot itemLabelSnapshot = itemLabel.toSnapshot();
		SubmittedItemLabel.Builder builder = SubmittedItemLabel.newBuilder()
				.withName(itemLabelSnapshot.getName());
		addQuestion(builder, itemLabelSnapshot);
		return builder.build();
	}

	private static void addQuestion(SubmittedItemLabel.Builder builder, ItemLabelSnapshot itemLabelSnapshot) {
		Optional.ofNullable(itemLabelSnapshot.getQuestion())
				.ifPresent(question -> {
					builder.withQuestion(
							SubmittedQuestionObjectMother.aSubmittedQuestion(itemLabelSnapshot.getQuestion()));
				});
	}
}
