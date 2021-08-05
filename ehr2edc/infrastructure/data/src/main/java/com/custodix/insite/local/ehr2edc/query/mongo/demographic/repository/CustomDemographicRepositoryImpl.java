package com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument;

final class CustomDemographicRepositoryImpl implements CustomDemographicRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	private CustomDemographicRepositoryImpl(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<DemographicDocument> findAll(Criteria criteria) {
		Query query = new Query(criteria);
		return mongoTemplate.find(query, DemographicDocument.class);
	}

	@Override
	public List<Document> findAllDocuments(Criteria criteria) {
		return mongoTemplate.find(new Query(criteria), Document.class, DemographicDocument.COLLECTION);
	}
}
