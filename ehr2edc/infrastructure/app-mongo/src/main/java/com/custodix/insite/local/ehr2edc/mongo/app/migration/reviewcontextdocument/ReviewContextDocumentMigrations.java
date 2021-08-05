package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Projections.include;

import java.util.function.BiConsumer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@ChangeLog(order = "ReviewContextDocument")
public class ReviewContextDocumentMigrations {
	private static final int BATCH_SIZE = 50;
	private static final Bson REVIEWED_CONTEXTS_WITHOUT_LABELED_VALUES = not(
			exists("reviewedEvent.reviewedForms.reviewedItemGroups.reviewedItems.labeledValue"));
	private static final Bson REVIEWED_CONTEXTS_WITHOUT_ITEM_INSTANCE_ID = not(
			exists("reviewedEvent.reviewedForms.reviewedItemGroups.reviewedItems.instanceId"));
	private static final String ID_KEY = "_id";
	private static final Bson ID_ONLY = include(ID_KEY);

	@ChangeSet(author = "Gert & Jan",
			   id = "ReviewContextDocument_copy_value_to_labeled_value",
			   order = "001")
	public void copyValuetoLabeledValue(MongoDatabase mongoDatabase) {
		applyMigration(mongoDatabase, REVIEWED_CONTEXTS_WITHOUT_LABELED_VALUES, this::copyValuetoLabeledValue);
	}

	@ChangeSet(author = "Jaron",
			   id = "ReviewContextDocument_add_reviewed_item_instance_id",
			   order = "002")
	public void addReviewedItemInstanceId(MongoDatabase mongoDatabase) {
		applyMigration(mongoDatabase, REVIEWED_CONTEXTS_WITHOUT_ITEM_INSTANCE_ID, this::addReviewedItemInstanceId);
	}

	private void applyMigration(MongoDatabase mongoDatabase, Bson query,
			BiConsumer<MongoCollection<Document>, Document> migration) {
		MongoCollection<Document> reviewedContextCollection = mongoDatabase.getCollection(
				ReviewContextDocument.COLLECTION);
		FindIterable<Document> ids = reviewedContextCollection.find(query)
				.projection(ID_ONLY);
		for (Document id : ids.batchSize(BATCH_SIZE)) {
			Document reviewContext = get(reviewedContextCollection, id);
			migration.accept(reviewedContextCollection, reviewContext);
		}
	}

	private void copyValuetoLabeledValue(MongoCollection<Document> reviewedContextCollection, Document original) {
		Document copy = copy(original);
		reviewedContextCollection.replaceOne(original, CopyValueToLabeledValue.forReviewContext(copy)
				.migrate());
	}

	private void addReviewedItemInstanceId(MongoCollection<Document> reviewedContextCollection, Document original) {
		Document copy = copy(original);
		reviewedContextCollection.replaceOne(original, AddReviewedItemInstanceId.forReviewContext(copy)
				.migrate());
	}

	private Document get(MongoCollection<Document> reviewedContextCollection, Document reviewContext) {
		return reviewedContextCollection.find(eq(ID_KEY, reviewContext.get(ID_KEY)))
				.first();
	}

	private Document copy(Document oldDoc) {
		return Document.parse(oldDoc.toJson());
	}
}



