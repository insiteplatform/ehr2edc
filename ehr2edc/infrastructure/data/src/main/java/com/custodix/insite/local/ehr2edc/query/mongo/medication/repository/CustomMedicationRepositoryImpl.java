package com.custodix.insite.local.ehr2edc.query.mongo.medication.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument;

final class CustomMedicationRepositoryImpl implements CustomMedicationRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	private CustomMedicationRepositoryImpl(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<MedicationDocument> findAll(Query query) {
		return mongoTemplate.find(query, MedicationDocument.class);
	}

	@Override
	public List<Document> findAllDocuments(Criteria criteria) {
		return mongoTemplate.find(new Query(criteria), Document.class, MedicationDocument.COLLECTION);
	}
}
