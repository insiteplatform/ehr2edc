package com.custodix.insite.local.ehr2edc.query.mongo.subjectmigrator.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.custodix.insite.local.ehr2edc.query.mongo.subjectmigrator.model.SubjectMigrationDocument;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface SubjectMigrationMongoRepository extends MongoRepository<SubjectMigrationDocument, String> {

	Optional<SubjectMigrationDocument> findBySubjectId(SubjectId subjectId);

}
