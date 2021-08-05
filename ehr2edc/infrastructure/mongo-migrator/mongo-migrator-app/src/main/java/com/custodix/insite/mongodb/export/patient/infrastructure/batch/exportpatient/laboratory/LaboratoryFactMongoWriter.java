package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument;

public final class LaboratoryFactMongoWriter implements ItemWriter<LaboratoryFact> {

	private final MongoTemplate mongoTemplate;

	LaboratoryFactMongoWriter(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void write(List<? extends LaboratoryFact> list) {
		list.stream()
				.map(LaboratoryFact::toDocument)
				.filter(Objects::nonNull)
				.forEach(document -> mongoTemplate.insert(document, LabValueDocument.COLLECTION));
	}
}
