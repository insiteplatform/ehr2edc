package com.custodix.insite.local.ehr2edc.query.mongo.medication.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument;

public interface CustomMedicationRepository {

	List<MedicationDocument> findAll(Query query);

	List<Document> findAllDocuments(Criteria criteria);

}
