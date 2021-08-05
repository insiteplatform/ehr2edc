package com.custodix.insite.local.ehr2edc.metadata.model;

import com.custodix.insite.local.ehr2edc.snapshots.ItemLabelSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.QuestionSnapshot;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemLabel;

import java.util.Locale;
import java.util.Optional;

public final class ItemLabel {
	private final String name;
	private final Question question;

	private ItemLabel(Builder builder) {
		name = builder.name;
		question = builder.question;
	}

	public static ItemLabel restoreFrom(ItemLabelSnapshot snapshot) {
		return newBuilder().withName(snapshot.getName()).withQuestion(getQuestion(snapshot)).build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public SubmittedItemLabel toReviewedLabel() {
		return SubmittedItemLabel.newBuilder()
				.withName(name)
				.withQuestion(Optional.ofNullable(question)
						.map(Question::toReviewedQuestion)
						.orElse(null))
				.build();
	}

	private static Question getQuestion(ItemLabelSnapshot snapshot) {
		return Optional.ofNullable(snapshot.getQuestion()).map(Question::restoreFrom).orElse(null);
	}

	public String getRepresentationFor(Locale locale) {
		return Optional.ofNullable(question)
				.flatMap(aQuestion -> aQuestion.getTranslatedText(locale)).orElse(name);
	}

	public ItemLabelSnapshot toSnapshot() {
		return new ItemLabelSnapshot(name, getQuestionSnapshot());
	}

	@Deprecated
	public String getName() {
		return name;
	}

	@Deprecated
	public Question getQuestion() {
		return question;
	}

	private QuestionSnapshot getQuestionSnapshot() {
		return Optional.ofNullable(question).map(Question::toSnapshot).orElse(null);
	}

	public static final class Builder {
		private String name;
		private Question question;

		private Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withQuestion(Question question) {
			this.question = question;
			return this;
		}

		public ItemLabel build() {
			return new ItemLabel(this);
		}
	}
}
