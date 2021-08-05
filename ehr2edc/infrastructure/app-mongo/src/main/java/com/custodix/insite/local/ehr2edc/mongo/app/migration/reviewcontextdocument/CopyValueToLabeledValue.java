package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument;

import static com.custodix.insite.local.ehr2edc.mongo.app.migration.MongoDocumentOperations.getArrayAsStream;

import java.util.stream.Stream;

import org.bson.Document;

final class CopyValueToLabeledValue {
	private static final String LABELED_VALUE_KEY = "labeledValue";
	private static final String VALUE_KEY_LEGACY = "value";
	private static final String REVIEWED_FORMS_KEY = "reviewedForms";
	private static final String REVIEWED_EVENT_KEY = "reviewedEvent";
	private static final String REVIEWED_ITEM_GROUPS_KEY = "reviewedItemGroups";
	private static final String REVIEWED_ITEMS_KEY = "reviewedItems";
	private static final String LABELED_VALUE_VALUE_KEY = "value";

	private final Document reviewContext;

	private CopyValueToLabeledValue(Document reviewContext) {
		this.reviewContext = reviewContext;
	}

	static CopyValueToLabeledValue forReviewContext(Document reviewContext) {
		return new CopyValueToLabeledValue(reviewContext);
	}

	public Document migrate() {
		Document reviewedEvent = extractReviewedEvent(reviewContext);
		extractItemDocuments(reviewedEvent).forEach(this::copyValueToLabeledValue);
		return reviewContext;
	}

	private Stream<Document> extractItemDocuments(Document reviewedEvent) {
		return getArrayAsStream(reviewedEvent, REVIEWED_FORMS_KEY).flatMap(this::extractItemGroups)
				.flatMap(this::extractItems);
	}

	private Document extractReviewedEvent(Document newDoc) {
		return newDoc.get(REVIEWED_EVENT_KEY, Document.class);
	}

	private Stream<Document> extractItemGroups(Document document) {
		return getArrayAsStream(document, REVIEWED_ITEM_GROUPS_KEY);
	}

	private Stream<Document> extractItems(Document document) {
		return getArrayAsStream(document, REVIEWED_ITEMS_KEY);
	}

	private void copyValueToLabeledValue(Document reviewedItem) {
		String value = reviewedItem.getString(VALUE_KEY_LEGACY);
		Document labeledValue = new Document();
		labeledValue.put(LABELED_VALUE_VALUE_KEY, value);
		reviewedItem.put(LABELED_VALUE_KEY, labeledValue);
	}
}
