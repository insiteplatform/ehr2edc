package com.custodix.insite.local.ehr2edc.submitted;

import java.util.Locale;
import java.util.Objects;

public final class SubmittedLabel {
	private final Locale locale;
	private final String text;

	private SubmittedLabel(Builder builder) {
		locale = builder.locale;
		text = builder.text;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Locale getLocale() {
		return locale;
	}

	public String getText() {
		return text;
	}

	public boolean matchesLocale(Locale locale) {
		return this.locale.equals(locale);
	}

	@Override
	public String toString() {
		return "SubmittedLabel{" + "locale=" + locale + ", text='" + text + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubmittedLabel label = (SubmittedLabel) o;
		return Objects.equals(locale, label.locale) && Objects.equals(text, label.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locale, text);
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

		public SubmittedLabel build() {
			return new SubmittedLabel(this);
		}
	}
}
