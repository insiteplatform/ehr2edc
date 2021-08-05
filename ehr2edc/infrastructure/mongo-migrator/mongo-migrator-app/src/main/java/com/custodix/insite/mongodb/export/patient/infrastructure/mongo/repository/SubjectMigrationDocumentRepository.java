package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Repository
public interface SubjectMigrationDocumentRepository extends MongoRepository<SubjectMigrationDocument, String> {

	SubjectMigrationDocument getBySubjectId(SubjectId subjectId);

	Optional<SubjectMigrationDocument> findBySubjectId(SubjectId subjectId);
}
