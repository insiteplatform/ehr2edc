package com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument;

@Repository
public interface DemographicRepository
		extends MongoRepository<DemographicDocument, String>, CustomDemographicRepository {
}
