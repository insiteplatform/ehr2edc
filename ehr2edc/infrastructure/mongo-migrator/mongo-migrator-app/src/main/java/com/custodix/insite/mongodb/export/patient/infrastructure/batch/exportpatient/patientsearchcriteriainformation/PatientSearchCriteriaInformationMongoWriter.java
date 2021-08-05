package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class PatientSearchCriteriaInformationMongoWriter implements ItemWriter<PatientSearchCriteriaInformation> {
	public static final String COLLECTION_NAME = "PatientId";
	public static final String FIELD_SOURCE = "source";
	public static final String FIELD_IDENTIFIER = "identifier";
	public static final String FIELD_BIRTH_DATE= "birthDate";

	private final MongoTemplate mongoTemplate;

	public PatientSearchCriteriaInformationMongoWriter(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void write(List<? extends PatientSearchCriteriaInformation> list) {
		list.forEach(this::writePatientIdentifier);
	}

	private void writePatientIdentifier(PatientSearchCriteriaInformation patientSearchCriteriaInformation) {
		PatientIdentifier patientIdentifier = patientSearchCriteriaInformation.getPatientIdentifier();
		Query query = new Query();
		query.addCriteria(Criteria.where(FIELD_SOURCE).is(patientIdentifier.getNamespace().getName()));
		query.addCriteria(Criteria.where(FIELD_IDENTIFIER).is(patientIdentifier.getPatientId().getId()));
		Update update = new Update();
		update.setOnInsert(FIELD_SOURCE, patientIdentifier.getNamespace().getName());
		update.setOnInsert(FIELD_IDENTIFIER, patientIdentifier.getPatientId().getId());
		update.set(FIELD_BIRTH_DATE, patientSearchCriteriaInformation.getBirthDate());
		mongoTemplate.upsert(query, update, COLLECTION_NAME);
	}
}
