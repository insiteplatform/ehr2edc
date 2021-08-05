package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Locale;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;

public final class LabelDocument {
	private final String locale;
	private final String text;

	@PersistenceConstructor
	private LabelDocument(String locale, String text) {
		this.locale = locale;
		this.text = text;
	}

	private LabelDocument(Builder builder) {
		locale = builder.locale;
		text = builder.text;
	}

	static LabelDocument toSnapshot(Label label) {
		return LabelDocument.newBuilder()
				.withLocale(label.getLocale()
						.toLanguageTag())
				.withText(label.getText())
				.build();
	}

	static Label restoreFrom(LabelDocument labelDocument) {
		return new Label(Locale.forLanguageTag(labelDocument.locale), labelDocument.text);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String locale;
		private String text;

		private Builder() {
		}

		public Builder withLocale(String locale) {
			this.locale = locale;
			return this;
		}

		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public LabelDocument build() {
			return new LabelDocument(this);
		}
	}
}
