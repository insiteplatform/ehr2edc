package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabel;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Locale;

public final class ReviewedLabelDocument {
	private final Locale locale;
	private final String text;

	@PersistenceConstructor
	ReviewedLabelDocument(Locale locale, String text) {
		this.locale = locale;
		this.text = text;
	}

	private ReviewedLabelDocument(Builder builder) {
		locale = builder.locale;
		text = builder.text;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getText() {
		return text;
	}

	static ReviewedLabelDocument toDocument(SubmittedLabel label) {
		return ReviewedLabelDocument.newBuilder()
				.withText(label.getText())
				.withLocale(label.getLocale())
				.build();
	}

	SubmittedLabel toReviewedLabel() {
		return SubmittedLabel.newBuilder()
				.withText(text)
				.withLocale(locale)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private Locale locale;
		private String text;

		private Builder() {
		}

		public Builder withLocale(Locale val) {
			locale = val;
			return this;
		}

		public Builder withText(String val) {
			text = val;
			return this;
		}

		public ReviewedLabelDocument build() {
			return new ReviewedLabelDocument(this);
		}
	}
}
