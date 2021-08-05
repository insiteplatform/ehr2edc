package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongo-migrator.db")
public class MigratorMongoDBConfigurationProperties {
	private MongoProperties mongoDb;

	public MongoProperties getMongoDb() {
		return mongoDb;
	}

	public void setMongoDb(MongoProperties mongoDb) {
		this.mongoDb = mongoDb;
	}
}
