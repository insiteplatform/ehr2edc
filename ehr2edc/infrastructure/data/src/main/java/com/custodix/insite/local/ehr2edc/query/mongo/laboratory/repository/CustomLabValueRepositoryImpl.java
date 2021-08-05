package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument;

final class CustomLabValueRepositoryImpl implements CustomLabValueRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	private CustomLabValueRepositoryImpl(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<LabValueDocument> findAll(Query query) {
		return mongoTemplate.find(query, LabValueDocument.class);
	}

	@Override
	public List<Document> findAllDocuments(Criteria criteria) {
		return mongoTemplate.find(new Query(criteria), Document.class, LabValueDocument.COLLECTION);
	}
}
