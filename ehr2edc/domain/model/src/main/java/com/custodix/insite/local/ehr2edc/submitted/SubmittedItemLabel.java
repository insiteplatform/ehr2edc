package com.custodix.insite.local.ehr2edc.submitted;

import java.util.Locale;
import java.util.Optional;

public final class SubmittedItemLabel {
	private final String name;
	private final SubmittedQuestion question;

	private SubmittedItemLabel(Builder builder) {
		name = builder.name;
		question = builder.question;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public SubmittedQuestion getQuestion() {
		return question;
	}

	public String getRepresentationFor(Locale locale) {
		return Optional.ofNullable(question)
				.flatMap(aQuestion -> aQuestion.getTranslatedText(locale)).orElse(name);
	}

	public static final class Builder {
		private String name;
		private SubmittedQuestion question;

		private Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withQuestion(SubmittedQuestion question) {
			this.question = question;
			return this;
		}

		public SubmittedItemLabel build() {
			return new SubmittedItemLabel(this);
		}
	}
}
