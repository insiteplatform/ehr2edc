package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.snapshots.QuestionSnapshot;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedQuestion;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class ReviewedQuestionMongoSnapshot {
	private final Map<String, String> translations;

	@PersistenceConstructor
	private ReviewedQuestionMongoSnapshot(Map<String, String> translations) {
		this.translations = translations;
	}

	private ReviewedQuestionMongoSnapshot(final Builder builder) {
		translations = builder.translations;
	}

	public static ReviewedQuestionMongoSnapshot fromSnapshot(SubmittedQuestion question) {
		return question == null ? null :
				new ReviewedQuestionMongoSnapshot(
						question.getTranslations().entrySet().stream().collect(Collectors.toMap(
								e -> e.getKey().toLanguageTag(), Map.Entry::getValue))

		);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public  QuestionSnapshot toSnapshot() {
		return new QuestionSnapshot(translations);
	}

	public Map<String, String> getTranslations() {
		return Collections.unmodifiableMap(translations);
	}

	public SubmittedQuestion toReviewQuestion() {
		return SubmittedQuestion.newBuilder()
				.withTranslations(
						translations.entrySet().stream().collect(Collectors.toMap(
								e -> Locale.forLanguageTag(e.getKey()), Map.Entry::getValue)
				))
				.build();
	}

	public static final class Builder {
		private Map<String, String> translations;

		private Builder() {
		}

		public Builder withTranslations(final Map<String, String> val) {
			translations = val;
			return this;
		}

		public ReviewedQuestionMongoSnapshot build() {
			return new ReviewedQuestionMongoSnapshot(this);
		}
	}
}
