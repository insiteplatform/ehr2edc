package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocument;

@Repository
public interface VitalSignRepository extends MongoRepository<VitalSignDocument, String>, CustomVitalSignRepository {
}
