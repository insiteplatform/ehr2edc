package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import java.util.Collection;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument;

public final class PatientFactMongoWriter implements ItemWriter<PatientFact> {

	private final MongoTemplate mongoTemplate;

	PatientFactMongoWriter(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void write(List<? extends PatientFact> list) {
		list.stream()
				.map(PatientFact::toDocuments)
				.flatMap(Collection::stream)
				.forEach(document -> mongoTemplate.insert(document, DemographicDocument.COLLECTION));
	}
}
