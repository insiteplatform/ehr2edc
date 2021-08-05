package com.custodix.insite.local.ehr2edc.query.executor.common.projector.model;

import java.util.Locale;
import java.util.Objects;

public final class Label {
	private final Locale locale;
	private final String text;

	public Label(Locale locale, String text) {
		this.locale = locale != null ? locale : Locale.ENGLISH;
		this.text = text;
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
		return "Label{" + "locale=" + locale + ", text='" + text + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Label label = (Label) o;
		return Objects.equals(locale, label.locale) && Objects.equals(text, label.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locale, text);
	}
}
