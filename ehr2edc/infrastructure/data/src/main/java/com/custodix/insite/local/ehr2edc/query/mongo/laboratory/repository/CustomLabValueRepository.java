package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument;

public interface CustomLabValueRepository {

	List<LabValueDocument> findAll(Query criteria);

	List<Document> findAllDocuments(Criteria criteria);

}
