package com.custodix.insite.local.ehr2edc.submitted;

import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import com.custodix.insite.local.ehr2edc.snapshots.QuestionSnapshot;

public class SubmittedQuestionObjectMother {
	public static SubmittedQuestion aDefaultSubmittedQuestion() {
		return aDefaultSubmittedQuestionBuilder().build();
	}

	public static SubmittedQuestion.Builder aDefaultSubmittedQuestionBuilder() {
		return SubmittedQuestion.newBuilder()
				.withTranslations(singletonMap(Locale.ENGLISH, "translation"));
	}

	public static SubmittedQuestion aSubmittedQuestion(QuestionSnapshot questionSnapshot) {
		Map<Locale, String> translations = new HashMap<>();
		questionSnapshot.getTranslatedText()
				.forEach(addToMap(translations));
		return SubmittedQuestion.newBuilder()
				.withTranslations(translations)
				.build();
	}

	private static BiConsumer<String, String> addToMap(Map<Locale, String> map) {
		return (locale, text) -> map.put(Locale.forLanguageTag(locale), text);
	}
}