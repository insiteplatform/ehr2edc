package com.custodix.insite.local.ehr2edc.mongo.app.migration.edcconnectiondocument;

import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.not;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

final class AddEDCSystem {
	private static final String EDC_SYSTEM_KEY = "edcSystem";
	private static final Bson EDC_CONNECTIONS_WITHOUT_EDC_SYSTEM = not(exists(EDC_SYSTEM_KEY));

	private final MongoDatabase mongoDatabase;

	private AddEDCSystem(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	static AddEDCSystem forDatabase(MongoDatabase mongoDatabase) {
		return new AddEDCSystem(mongoDatabase);
	}

	void migrate() {
		MongoCollection<Document> collection = mongoDatabase.getCollection(EDCConnectionDocument.COLLECTION);
		collection.updateMany(EDC_CONNECTIONS_WITHOUT_EDC_SYSTEM, Updates.set(EDC_SYSTEM_KEY, EDCSystem.RAVE.name()));
	}
}
