package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument;

@Repository
public interface ObservationSummaryDocumentRepository extends MongoRepository<ObservationSummaryDocument, String> {

}
