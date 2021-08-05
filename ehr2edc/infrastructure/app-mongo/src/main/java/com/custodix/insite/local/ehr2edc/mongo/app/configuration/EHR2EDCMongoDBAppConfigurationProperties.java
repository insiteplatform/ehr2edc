package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ehr2edc.db")
public class EHR2EDCMongoDBAppConfigurationProperties {
	private MongoProperties mongoDbApp;

	public MongoProperties getMongoDbApp() {
		return mongoDbApp;
	}

	public void setMongoDbApp(MongoProperties mongoDbApp) {
		this.mongoDbApp = mongoDbApp;
	}
}
