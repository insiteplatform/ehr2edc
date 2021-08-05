package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.QuestionSnapshot;

public final class QuestionDocument {
	private final Map<String, String> translatedText;

	@PersistenceConstructor
	private QuestionDocument(Map<String, String> translatedText) {
		this.translatedText = translatedText;
	}

	public static QuestionDocument fromSnapshot(QuestionSnapshot question) {
		return question == null ? null : new QuestionDocument(question.getTranslatedText());
	}

	public  QuestionSnapshot toSnapshot() {
		return new QuestionSnapshot(translatedText);
	}

	public Map<String, String> getTranslatedText() {
		return Collections.unmodifiableMap(translatedText);
	}
}
