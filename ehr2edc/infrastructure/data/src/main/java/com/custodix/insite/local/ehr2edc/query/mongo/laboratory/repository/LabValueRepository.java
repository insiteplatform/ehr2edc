package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument;

@Repository
public interface LabValueRepository extends MongoRepository<LabValueDocument, String>, CustomLabValueRepository {
}
