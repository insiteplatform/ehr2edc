package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocument;

final class CustomVitalSignRepositoryImpl implements CustomVitalSignRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	private CustomVitalSignRepositoryImpl(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<VitalSignDocument> findAll(Query query) {
		return mongoTemplate.find(query, VitalSignDocument.class);
	}

	@Override
	public List<Document> findAllDocuments(Criteria criteria) {
		return mongoTemplate.find(new Query(criteria), Document.class, VitalSignDocument.COLLECTION);
	}
}
