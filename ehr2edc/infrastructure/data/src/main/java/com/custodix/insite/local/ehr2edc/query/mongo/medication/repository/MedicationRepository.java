package com.custodix.insite.local.ehr2edc.query.mongo.medication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument;

@Repository
public interface MedicationRepository extends MongoRepository<MedicationDocument, String>, CustomMedicationRepository {
}
