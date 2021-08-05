package com.custodix.insite.local.ehr2edc.query.mongo.configuration;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ehr2edc.db")
class EHR2EDCMongoDBQueryProperties {
	private MongoProperties mongoDbQuery;

	public MongoProperties getMongoDbQuery() {
		return mongoDbQuery;
	}

	public void setMongoDbQuery(MongoProperties mongoDbQuery) {
		this.mongoDbQuery = mongoDbQuery;
	}
}
