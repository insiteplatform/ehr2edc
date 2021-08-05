package com.custodix.insite.local.ehr2edc.mongo.app.migration;

import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;

public final class MongoDocumentOperations {
	private MongoDocumentOperations() {
	}

	@SuppressWarnings({ "unchecked" }) //NOSONAR
	public static Stream<Document> getArrayAsStream(Document document, String key) {
		return document.get(key, List.class)
				.stream();
	}
}
