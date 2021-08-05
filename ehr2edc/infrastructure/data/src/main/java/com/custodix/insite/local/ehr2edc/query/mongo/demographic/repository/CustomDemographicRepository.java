package com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument;

public interface CustomDemographicRepository {

	List<DemographicDocument> findAll(Criteria criteria);

	List<Document> findAllDocuments(Criteria criteria);
}
