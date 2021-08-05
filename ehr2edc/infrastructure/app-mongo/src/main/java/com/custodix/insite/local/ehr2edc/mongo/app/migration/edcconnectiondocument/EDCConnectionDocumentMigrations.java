package com.custodix.insite.local.ehr2edc.mongo.app.migration.edcconnectiondocument;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

@ChangeLog(order = "EDCConnectionDocument")
public class EDCConnectionDocumentMigrations {

	@ChangeSet(author = "Jaron",
			   id = "EDCConnectionDocument_add_edc_system",
			   order = "001")
	public void addEDCSystem(MongoDatabase mongoDatabase) {
		AddEDCSystem.forDatabase(mongoDatabase).migrate();
	}

}