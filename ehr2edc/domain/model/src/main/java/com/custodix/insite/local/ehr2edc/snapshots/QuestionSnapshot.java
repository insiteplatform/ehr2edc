package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class QuestionSnapshot {
	private final Map<String, String> translatedText;

	public QuestionSnapshot(final Map<String, String> map) {
		translatedText = new HashMap<>(map);
	}

	public Map<String, String> getTranslatedText() {
		return Collections.unmodifiableMap(translatedText);
	}
}
