package com.custodix.insite.local.ehr2edc.query.fhir.mongo.configuration;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ehr2edc.db")
class Ehr2EdcFhirDbProperties {
	private MongoProperties fhirDb;

	public MongoProperties getFhirDb() {
		return fhirDb;
	}

	public void setFhirDb(MongoProperties fhirDb) {
		this.fhirDb = fhirDb;
	}
}
