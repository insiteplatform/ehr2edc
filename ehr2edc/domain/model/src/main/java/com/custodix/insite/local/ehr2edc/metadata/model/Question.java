package com.custodix.insite.local.ehr2edc.metadata.model;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.QuestionSnapshot;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedQuestion;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class Question {
	private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("");

	private final Map<Locale, String> translatedText;

	public Question(Map<Locale, String> map) {
		ensureThereIsAtLeastOneTranslatedText(map);
		this.translatedText = new HashMap<>(map);
	}

	public static Question restoreFrom(QuestionSnapshot question) {
		return question == null ? null : restoreFromSnapshot(question);
	}

	SubmittedQuestion toReviewedQuestion() {
		return SubmittedQuestion.newBuilder()
				.withTranslations(translatedText)
				.build();
	}

	private static void ensureThereIsAtLeastOneTranslatedText(Map<Locale, String> map) {
		if (map.isEmpty()) {
			throw DomainException.getInstance(DomainException.Type.NOT_NULL, "translated text");
		}
	}

	private static Question restoreFromSnapshot(QuestionSnapshot question) {
		Map<Locale, String> map = new HashMap<>();
		question.getTranslatedText().forEach(addToMap(map));
		return new Question(map);
	}

	public Optional<String> getTranslatedText(Locale locale) {
		return Optional.ofNullable(translatedText.getOrDefault(locale, getTranslatedTextForLanguageOrDefault(locale)));
	}

	public QuestionSnapshot toSnapshot() {
		Map<String, String> map = new HashMap<>();
		translatedText.forEach((locale, s) -> map.put(locale.toLanguageTag(), s));
		return new QuestionSnapshot(map);
	}

	private static BiConsumer<String, String> addToMap(Map<Locale, String> map) {
		return (locale, text) -> map.put(Locale.forLanguageTag(locale), text);
	}

	private String getTranslatedTextForLanguageOrDefault(Locale locale) {
		Locale localeForLanguage = new Locale(locale.getLanguage());
		return translatedText.getOrDefault(localeForLanguage, getDefaultTranslatedText());
	}

	private String getDefaultTranslatedText() {
		return translatedText.get(DEFAULT_LOCALE);
	}
}
