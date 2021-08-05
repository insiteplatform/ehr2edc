package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument;

import static com.custodix.insite.local.ehr2edc.mongo.app.migration.MongoDocumentOperations.getArrayAsStream;

import java.util.UUID;
import java.util.stream.Stream;

import org.bson.Document;

final class AddReviewedItemInstanceId {
	private static final String REVIEWED_FORMS_KEY = "reviewedForms";
	private static final String REVIEWED_EVENT_KEY = "reviewedEvent";
	private static final String REVIEWED_ITEM_GROUPS_KEY = "reviewedItemGroups";
	private static final String REVIEWED_ITEMS_KEY = "reviewedItems";
	private static final String INSTANCE_ID_KEY = "instanceId";

	private final Document reviewContext;

	private AddReviewedItemInstanceId(Document reviewContext) {
		this.reviewContext = reviewContext;
	}

	static AddReviewedItemInstanceId forReviewContext(Document reviewContext) {
		return new AddReviewedItemInstanceId(reviewContext);
	}

	public Document migrate() {
		extractItemsFromReviewContext(reviewContext).forEach(this::addInstanceId);
		return reviewContext;
	}

	private void addInstanceId(Document reviewedItem) {
		reviewedItem.put(INSTANCE_ID_KEY, UUID.randomUUID()
				.toString());
	}

	private Stream<Document> extractItemsFromReviewContext(Document reviewContext) {
		Document reviewedEvent = extractReviewedEvent(reviewContext);
		return extractForms(reviewedEvent).flatMap(this::extractItemGroups)
				.flatMap(this::extractItems);
	}

	private Document extractReviewedEvent(Document reviewContext) {
		return reviewContext.get(REVIEWED_EVENT_KEY, Document.class);
	}

	private Stream<Document> extractForms(Document reviewedEvent) {
		return getArrayAsStream(reviewedEvent, REVIEWED_FORMS_KEY);
	}

	private Stream<Document> extractItemGroups(Document reviewedForm) {
		return getArrayAsStream(reviewedForm, REVIEWED_ITEM_GROUPS_KEY);
	}

	private Stream<Document> extractItems(Document reviewedItemGroup) {
		return getArrayAsStream(reviewedItemGroup, REVIEWED_ITEMS_KEY);
	}
}
