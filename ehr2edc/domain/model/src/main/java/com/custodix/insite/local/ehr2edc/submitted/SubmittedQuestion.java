package com.custodix.insite.local.ehr2edc.submitted;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class SubmittedQuestion {
	private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("");

	private final Map<Locale, String> translations;

	public SubmittedQuestion(Map<Locale, String> map) {
		this.translations = new HashMap<>(map);
	}

	private SubmittedQuestion(final Builder builder) {
		translations = builder.translations;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Map<Locale, String> getTranslations() {
		return translations;
	}

	public Optional<String> getTranslatedText(Locale locale) {
		return Optional.ofNullable(translations.getOrDefault(locale, getTranslatedTextForLanguageOrDefault(locale)));
	}


	private String getTranslatedTextForLanguageOrDefault(Locale locale) {
		Locale localeForLanguage = new Locale(locale.getLanguage());
		return translations.getOrDefault(localeForLanguage, getDefaultTranslatedText());
	}

	private String getDefaultTranslatedText() {
		return translations.get(DEFAULT_LOCALE);
	}

	public static final class Builder {
		private Map<Locale, String> translations;

		private Builder() {
		}

		public Builder withTranslations(final Map<Locale, String> val) {
			translations = val;
			return this;
		}

		public SubmittedQuestion build() {
			return new SubmittedQuestion(this);
		}
	}
}
