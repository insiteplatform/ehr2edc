package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocument;

public interface CustomVitalSignRepository {

	List<VitalSignDocument> findAll(Query query);

	List<Document> findAllDocuments(Criteria criteria);

}
