package com.custodix.insite.local.ehr2edc.query.mongo.patient.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.local.ehr2edc.query.mongo.patient.model.PatientIdDocument;

public class PatientIdCustomRepositoryImpl implements PatientIdCustomRepository {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public PatientIdCustomRepositoryImpl(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<String> findDistinctSources() {
		return mongoTemplate.getCollection(PatientIdDocument.COLLECTION)
				.distinct("source", String.class)
				.into(new ArrayList<>());
	}
}
